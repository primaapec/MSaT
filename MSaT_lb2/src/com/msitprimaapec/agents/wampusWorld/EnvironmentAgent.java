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

public class EnvironmentAgent extends Agent {

    private AID spelAgent;

    @Override
    protected void setup() {
        System.out.println("Environment agent " + getAID().getName() + " is ready!");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Wumpus-World-Cave");
        sd.setName("Cave-wandering");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new RequestBehavior());
    }
    
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Environment agent terminating...");
    }
    private class RequestBehavior extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (spelAgent == null)
                    spelAgent = msg.getSender();
                if (spelAgent.equals(msg.getSender())) {
                    if (msg.getPerformative() == ACLMessage.REQUEST)
                        myAgent.addBehaviour(new PerceptReplyBehaviour(msg));
                    if (msg.getPerformative() == ACLMessage.CFP)
                        myAgent.addBehaviour(new WorldChangingBehaviour(msg));
                }
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

    private class PerceptReplyBehaviour extends OneShotBehaviour {

        ACLMessage msg;

        public PerceptReplyBehaviour(ACLMessage m)
        {
            super();
            msg = m;
        }

        public void action() {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(GeneratePerceptSequence());
            myAgent.send(reply);
        }

        private String GeneratePerceptSequence() {
            String reply = "";
            // TODO generate percept string
            return reply;
        }
    }

    private class WorldChangingBehaviour extends OneShotBehaviour {

        ACLMessage msg;

        public WorldChangingBehaviour(ACLMessage m)
        {
            super();
            msg = m;
        }

        public void action() {
            String content = msg.getContent();
            // TODO process message and change the world
        }
    }
}
