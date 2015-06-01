package unity.jms;

import javax.jms.*;
import javax.naming.*;

public class Sender implements MessageListener{
	private String test;
	private QueueConnection qConnect = null;
	private QueueSession qSession = null;
	private Queue fila = null;
	
	public Sender(String queuecf, String fila){
		
		try
		{
			//Conecta ao provedor e pega a conexão JMS
			Context ctx = new InitialContext();
			QueueConnectionFactory qFactory = (QueueConnectionFactory)ctx.lookup(queuecf);
			qConnect =qFactory.createQueueConnection();
			
			//Cria sessão JMS
			qSession = qConnect.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			
			//procurar fila
			this.fila = (Queue)ctx.lookup(fila);
			
			//Começar conexão
			qConnect.start();
                        
            QueueReceiver qReceiver= qSession.createReceiver(this.fila);
            qReceiver.setMessageListener(this);					
		}
		catch(JMSException e)
		{
			System.out.print("EXCEÇÃO - ERRO ao criar conexão: "+e);
		}
		catch(NamingException e)
		{
			System.out.print("EXCEÇÃO - ERRO de nomes: "+e);
		}
	}
	
	private void enviarMensagem(){
		try{
			TextMessage msg = qSession.createTextMessage();
			msg.setText("Funfou!!");
			
			QueueSender qSender = qSession.createSender(fila);
			qSender.send(msg);
		}
		catch(JMSException e){
			System.out.print("EXCEÇÃO ao enviar mensagem - ERRO: "+e);
		}
                catch(Exception e){
                    System.out.println("ERRO: "+e);
                }
	}
	
	private void exit(){
		try{
			qConnect.close();
		}
		catch(JMSException e){
			System.out.print("ERRO ao tentar SAIR:"+e);
		}
	}
	
        public String retorno(){
            Sender myJava = new Sender("jms/__defaultConnectionFactory","fila");
            myJava.enviarMensagem();
            return this.test;
        }
        
	public static void main(String[] args){
		Sender myJava = new Sender("jms/__defaultConnectionFactory","fila");
		myJava.enviarMensagem();
		System.out.print(myJava.test);
		
        myJava.exit();
               
	
	}

    @Override
    public void onMessage(Message message) {
        try{
            TextMessage msg = (TextMessage)message;
            String texto =msg.getText();
            System.out.println(texto);
        }catch(JMSException e){
            System.out.println("erro"+e);
        }
        catch(Exception e){
                    System.out.println("ERRO: "+e);
                }
        
    }
}
