import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/** つまらないリスナー */
@Component
public class JMSListener implements MessageListener {

	public JMSListener(){
		System.out.println(this + "■\t初期化");
	}

	@Autowired
	JmsTemplate jmst;

	@Override
	public void onMessage(Message message) {
		try {
			Object obj = jmst.getMessageConverter().fromMessage(message);
			System.out.println(this + "■" + obj);
		} catch (JMSException e) {
		}
	}
}
