package io.axoniq.demo.memories.service.shopping.infra;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.unitofwork.UnitOfWork;

public class CommandLogger implements MessageHandlerInterceptor<CommandMessage<?>> {

    @Override
    public Object handle(UnitOfWork<? extends CommandMessage<?>> unitOfWork, InterceptorChain interceptorChain) throws Exception {

        String command  = unitOfWork.getMessage().getCommandName();

        System.out.println("\n\n==================================================");
        System.out.println("📟  System received a message: " + command);
        System.out.println("--------------------------------------------------");

        interceptorChain.proceed();

        System.out.println("--------------------------------------------------");
        System.out.println("📟  Message was successfully processed");
        System.out.println("==================================================");

        return null;
    }
}
