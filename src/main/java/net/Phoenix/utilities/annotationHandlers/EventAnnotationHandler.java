package net.Phoenix.utilities.annotationHandlers;

import net.Phoenix.utilities.annotations.Event;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class EventAnnotationHandler {
    public static void registerEvents(JDA jda){
    }

    public static class EventHandler implements EventListener {
        public Method eventMethod;
        public Event annotation;

        public EventHandler(Method eventMethod, Event annotation) {
            this.eventMethod = eventMethod;
            this.annotation = annotation;
        }


        @Override
        public void onEvent(@NotNull GenericEvent event) {
            Class<? extends net.dv8tion.jda.api.events.Event> eventtype = annotation.eventType();
            if(event instanceof eventtype){
                eventMethod.invoke(eventMethod.getDeclaringClass(), (annotation.eventType()) event);
            }
        }


    }

}
