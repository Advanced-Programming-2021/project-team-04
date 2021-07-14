package yugioh.utils;

import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public abstract class DragUtils {

    private static final Double ON_DRAG_RELEASE_SPEED = 400.0;

    public static double getDurationByCoordination(double x1, double y1, double x2, double y2) {
        x1 -= x2;
        y1 -= y2;
        return Math.sqrt(x1 * x1 + y1 * y1) / ON_DRAG_RELEASE_SPEED;
    }

    public static void playGoingBackTransition(Node dragNode, List<Double> coordination) {
        var transition = new TranslateTransition();
        transition.setNode(dragNode);
        transition.setToX(coordination.get(0));
        transition.setToY(coordination.get(1));
        transition.setDuration(Duration.seconds(getDurationByCoordination(coordination.get(0), coordination.get(1), dragNode.getTranslateX(), dragNode.getTranslateY())));
        transition.play();
    }

    public enum Event {
        DRAG_START, DRAG, DRAG_END
    }

    public interface Listener {
        void accept(DragHandler dragHandler, Event dragEvent);
    }

    @Getter
    @Setter
    public static final class DragHandler implements EventHandler<MouseEvent> {
        private final Node eventNode;
        private final Map<Node, List<Double>> dragNodes = new LinkedHashMap<>();
        private final List<Listener> dragListeners = new ArrayList<>();
        private double lastMouseX = 0;
        private double lastMouseY = 0;
        private boolean dragging = false;

        public DragHandler(final Node node) {
            this(node, node);
        }

        public DragHandler(final Node eventNode, final Node... dragNodes) {
            this.eventNode = eventNode;
            Arrays.stream(dragNodes).forEach(node -> this.dragNodes.put(node, List.of(node.getTranslateX(), node.getTranslateY())));
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        public final void addListener(final Listener listener) {
            this.dragListeners.add(listener);
        }

        @Override
        public final void handle(final MouseEvent event) {
            if (MouseEvent.MOUSE_PRESSED == event.getEventType()) {
                if (this.eventNode.contains(event.getX(), event.getY())) {
                    dragNodes.keySet().forEach(Node::toFront);
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();
                    event.consume();
                }
            } else if (MouseEvent.MOUSE_DRAGGED == event.getEventType()) {
                if (!this.dragging) {
                    this.dragging = true;
                    this.dragListeners.forEach(l -> l.accept(this, DragUtils.Event.DRAG_START));
                }
                this.dragNodes.keySet().forEach(node -> {
                    node.setTranslateX(node.getTranslateX() + event.getSceneX() - this.lastMouseX);
                    node.setTranslateY(node.getTranslateY() + event.getSceneY() - this.lastMouseY);
                });
                this.lastMouseX = event.getSceneX();
                this.lastMouseY = event.getSceneY();
                event.consume();
                this.dragListeners.forEach(l -> l.accept(this, Event.DRAG));
            } else if (MouseEvent.MOUSE_RELEASED == event.getEventType() && this.dragging) {
                event.consume();
                this.dragging = false;
                this.dragListeners.forEach(l -> l.accept(this, Event.DRAG_END));
            }
        }
    }
}
