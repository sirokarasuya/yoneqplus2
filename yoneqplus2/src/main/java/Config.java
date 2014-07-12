import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConversionException;

/**
 * Springコンフィギュレーション
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Config {

	static String DEST = "yoneq-dest";

	@Autowired
	ConfigurableApplicationContext context;

	@Autowired
	ConnectionFactory confactory;

	@Autowired
	JmsTemplate jmst;

	/** つまらないリスナーを登録したリスナーコンテナ */
	@Bean
	SimpleMessageListenerContainer container1() {
		return createContainer("JMSListener");
	}

	/** ラムダなリスナー */
	@Bean
	MessageListener lamdaListener() {
		return ms -> {
			try {
				Object obj = jmst.getMessageConverter().fromMessage(ms);
				System.out.println("lamdaListener@" + this + "■" + obj);
			} catch (MessageConversionException | JMSException e) {
			}
		};
	}

	/** ラムダなリスナーを登録したリスナーコンテナ */
	@Bean
	SimpleMessageListenerContainer container2() {
		return createContainer("lamdaListener");
	}

	/** LoggerInfoレベルでアダプトしたリスナーアダプタ */
	@Bean
	MessageListenerAdapter listenerAdapter() {
		MessageListenerAdapter mla = new MessageListenerAdapter(
				LogFactory.getLog(getClass()));
		mla.setDefaultListenerMethod("warn");
		return mla;
	}

	/** アダプターを登録したリスナーコンテナ */
	@Bean
	SimpleMessageListenerContainer container3() {
		return createContainer("listenerAdapter");
	}

	SimpleMessageListenerContainer createContainer(String beanName) {
		SimpleMessageListenerContainer ct = new SimpleMessageListenerContainer();
		ct.setMessageListener(context.getBean(beanName));
		ct.setConnectionFactory(confactory);
		ct.setDestinationName(Config.DEST);
		ct.setPubSubDomain(jmst.isPubSubDomain());
		return ct;
	}

	/** パブリッシャー */
	@Bean
	//	@Scope("prototype")
	@Scope("singleton")
	Runnable publisher() {
		return () -> {
			String time = LocalTime.now().toString();
			Map<String, String> timeMap = new HashMap<String, String>();
			timeMap.put("time", time);
			String msg = new Random().nextBoolean() ? time : timeMap.toString();
			System.out.println("\nSend a message. <" + msg + ">" + this);
			jmst.convertAndSend(Config.DEST, msg);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		};
	}
}
