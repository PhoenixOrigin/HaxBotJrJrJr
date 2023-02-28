package net.Phoenix.utilities.annotationHandlers;

import net.Phoenix.utilities.annotations.Event;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class EventAnnotationHandler {
    public static void registerEvents(JDA jda){
        Set<Method> methodsAnnotatedWith = new Reflections("net.Phoenix", new MethodAnnotationsScanner()).getMethodsAnnotatedWith(Event.class);
        for (Method method : methodsAnnotatedWith) {
            jda.addEventListener(new EventHandler(method, method.getAnnotation(Event.class), method.getDeclaringClass()));
        }
    }

    public static class EventHandler implements EventListener {
        public Method eventMethod;
        public Event annotation;
        public Class<?> eventClass;

        public EventHandler(Method eventMethod, Event annotation, Class<?> eventClass) {
            this.eventMethod = eventMethod;
            this.annotation = annotation;
            this.eventClass = eventClass;
        }


        @Override
        public void onEvent(@NotNull GenericEvent event) {
            if(event.getClass().getTypeName().equals(annotation.eventType().getTypeName())){
                try {
                    eventMethod.invoke(eventMethod.getDeclaringClass().getDeclaredConstructor().newInstance(), event);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

}
