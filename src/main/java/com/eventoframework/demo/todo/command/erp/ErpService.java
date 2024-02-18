package com.eventoframework.demo.todo.command.erp;

import com.evento.common.modeling.annotations.component.Service;
import com.evento.common.modeling.annotations.handler.CommandHandler;
import com.evento.common.utils.Sleep;
import com.eventoframework.demo.todo.api.erp.command.ErpUserActivityRegisterCommand;
import com.eventoframework.demo.todo.api.erp.event.ErpUserActivityRegisteredEvent;

@Service
public class ErpService {

    @CommandHandler
    public ErpUserActivityRegisteredEvent handle(ErpUserActivityRegisterCommand command){
        System.out.println("------");
        System.out.println("Activity Registration for resource "+ command.getResourceType() + " with ID: " + command.getResourceIdentifier());
        command.getContributions().forEach((u,a) -> {
            Sleep.apply(1000);
            System.out.printf("%d Activity Registered for user %s %n", a, u);
        });
        System.out.println("------");
        return new ErpUserActivityRegisteredEvent(command.getResourceType(),
                command.getResourceIdentifier(),
                command.getContributions());
    }
}
