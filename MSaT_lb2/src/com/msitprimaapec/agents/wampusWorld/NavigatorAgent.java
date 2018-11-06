package com.msitprimaapec.agents.wampusWorld;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jdk.jshell.spi.ExecutionControl;

public class NavigatorAgent extends Agent {

    private AID spelAgent;

    @Override
    protected void setup() {
        System.out.println("Navigator agent " + getAID().getLocalName() + " is ready!");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Wumpus-World-Navigator");
        sd.setName("Wumpus-Gold-finder");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new PerceptRequestBehavior());
    }
    private class PerceptRequestBehavior extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                if (spelAgent == null)
                    spelAgent = msg.getSender();
                if (spelAgent.equals(msg.getSender()))
                    myAgent.addBehaviour(new CreateSendPropose(msg));
                else {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
            }
            else
            {
                block();
            }
        }
    }

    private class CreateSendPropose extends OneShotBehaviour {

        ACLMessage msg;

        public  CreateSendPropose(ACLMessage m)
        {
            super();
            msg = m;
        }

        public void action() {
            if (msg != null) {
                String content = msg.getContent();
                ACLMessage reply = msg.createReply();
                if (content != null) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(ResponseAction(content));
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            }
            else
            {
                block();
            }
        }

        private String ResponseAction(String content) {
            String reply = "";
            // TODO process message and generate action
            return reply;
        }
    }
}
