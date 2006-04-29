#place your comments here - this is just a description for your own purposes.
[when]There is a customer ticket with status of "{status}"=customer : Customer( )   ticket : Ticket( customer == customer, status == "{status}" )
[when]There is a "{subscription}" customer with a ticket status of "{status}"=customer : Customer(subscription == "{subscription}") ticket : Ticket( customer == customer, status == "{status}")
[then]Log "{message}"=System.out.println("{message} " + ticket);
[then]Escalate the ticket=ticket.setStatus("Escalate"); modify(ticket);
[then]Send escalation email=sendEscalationEmail( customer, ticket );
