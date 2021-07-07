package yugioh.view;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class DragUtils {

    public enum Event {
        NONE, DRAG_START, DRAG, DRAG_END
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
        private double lastMouseY = 0; // scene coords
        private boolean dragging = false;

        public DragHandler(final Node node) {
            this(node, node);
        }

        public DragHandler(final Node eventNode, final Node... dragNodes) {
            this.eventNode = eventNode;
            Arrays.stream(dragNodes).forEach(node -> this.dragNodes.put(node, List.of(node.getTranslateX(), node.getTranslateY())));
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        public final boolean addDraggedNode(final Node node) {
            if (!this.dragNodes.containsKey(node)) {
                this.dragNodes.put(node, List.of(node.getTranslateX(), node.getTranslateY()));
                return true;
            }
            return false;
        }

        public final void addListener(final Listener listener) {
            this.dragListeners.add(listener);
        }

        public final void detatch() {
            this.eventNode.removeEventFilter(MouseEvent.ANY, this);
        }

        @Override
        public final void handle(final MouseEvent event) {
            if (MouseEvent.MOUSE_PRESSED == event.getEventType()) {
                if (this.eventNode.contains(event.getX(), event.getY())) {
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

        public final void removeDraggedNode(final Node node) {
            this.dragNodes.remove(node);
        }

        public final boolean removeListener(final Listener listener) {
            return this.dragListeners.remove(listener);
        }
    }
}
