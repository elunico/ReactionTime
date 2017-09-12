package reactiontime_kt

import javafx.event.Event
import javafx.event.EventHandler

/**
 * @author Thomas Povinelli
 * Created 2017-Sep-12
 * In ReactionTime
 */
object NoAction: EventHandler<Event> {
    override fun handle(event: Event?) = Unit // do nothing
}
