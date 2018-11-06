package com.msitprimaapec.agents.wampusWorld;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SpeleologistAgent extends Agent {

    private AID na; // NavigatorAgent
    private AID ea; // EnvironmentAgent


    @Override
    protected void setup() {
        System.out.println("Speleologist agent " + getAID().getLocalName() + " is ready!");
        addBehaviour(new WakerBehaviour(this,5000) {
            @Override
            protected void onWake() {
                DFAgentDescription templateNavi = new DFAgentDescription();
                DFAgentDescription templateCave = new DFAgentDescription();
                ServiceDescription sdNavi = new ServiceDescription();
                ServiceDescription sdCave = new ServiceDescription();
                sdNavi.setType("Wumpus-World-Navigator");
                sdCave.setType("Wumpus-World-Cave");
                templateNavi.addServices(sdNavi);
                templateCave.addServices(sdCave);
                try {
                    na = DFService.search(myAgent, templateNavi)[0].getName();
                    ea = DFService.search(myAgent, templateCave)[0].getName();
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                myAgent.addBehaviour(new CaveWanderingBehaviour());
            }
        });

    }

    @Override
    protected void takeDown() {
        System.out.println("Speleologist agent terminating...");
    }

    private class CaveWanderingBehaviour extends Behaviour {

        private int step = 0;
        private MessageTemplate mt;
        private String message;

        @Override
        public void action() {
            switch (step) {
                case 0:
                    ACLMessage requestPercept = new ACLMessage(ACLMessage.REQUEST);
                    requestPercept.addReceiver(ea);
                    requestPercept.setConversationId("percept");
                    myAgent.send(requestPercept);
                    mt = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("percept"),
                            MessageTemplate.MatchInReplyTo(requestPercept.getReplyWith()));
                    step++;
                    break;
                case 1:
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            message = PrepareSentance(reply.getContent());
                            step++;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    ACLMessage askForAction = new ACLMessage(ACLMessage.REQUEST);
                    askForAction.addReceiver(na);
                    askForAction.setContent(message);
                    askForAction.setConversationId("Ask-for-action");
                    step++;
                    mt = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("Ask-for-action"),
                            MessageTemplate.MatchInReplyTo(askForAction.getReplyWith()));
                    step++;
                    break;
                case 3:
                    ACLMessage reply2 = myAgent.receive(mt);
                    if (reply2 != null) {
                        if (reply2.getPerformative() == ACLMessage.PROPOSE) {
                            message = ProcessSentance(reply2.getContent());
                            step++;
                        }
                    } else {
                        block();
                    }
                    step++;
                    break;
                case 4:
                    ACLMessage action = new ACLMessage(ACLMessage.CFP);
                    action.addReceiver(ea);
                    action.setContent(message);
                    action.setConversationId("action");
                    myAgent.send(action);
                    mt = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("action"),
                            MessageTemplate.MatchInReplyTo(action.getReplyWith()));
                    step++;
                    break;
                case 5:
                    if (message == "Climb") {
                        step++;
                        doDelete();
                        return;
                    }
                    else
                        step=0;
                    break;
            }
        }

        private String ProcessSentance(String content) {
            // TODO process action message and generate action message for environment
            return "";
        }

        private String PrepareSentance(String content) {
            StringBuilder temp = new StringBuilder();
            for (String s: content.split(" ")) {
                switch (s) {
                    // TODO generating environment state message
                    default:
                        temp.append("None");
                }
            }
            return temp.toString();
        }

        @Override
        public boolean done() {
            return step == 6;
        }
    }
}
