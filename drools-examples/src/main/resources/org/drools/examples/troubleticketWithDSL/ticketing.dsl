[condition][]There is a customer ticket with status of "{status}"=customer : Customer( )   ticket : Ticket( customer == customer, status == "{status}" )
[condition][]There is a "{subscription}" customer with a ticket status of "{status}"=customer : Customer(subscription == "{subscription}") ticket : Ticket( customer == customer, status == "{status}")
[consequence][]Log "{message}"=System.out.println("{message} ");
[consequence][]Escalate the ticket=ticket.setStatus("Escalate");update(ticket);
[consequence][]Send escalation email=sendEscalationEmail( customer, ticket );
