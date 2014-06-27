import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.FileSystemUtils;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		// ACMQの一時領域削除
		FileSystemUtils.deleteRecursively(new File("activemq-data"));

		// アプリケーション起動
		ConfigurableApplicationContext context = SpringApplication.run(Config.class);

		ExecutorService ex = Executors.newFixedThreadPool(2);
		for (int i = 0; i < 6; i++) {
			Runnable r = context.getBean(Runnable.class);
			System.out.println("Runnable■"+ r.toString());
			ex.execute(r);

		}
		ex.shutdown();

		Thread.sleep(5000);

		// リソースクローズ
		context.close();
		FileSystemUtils.deleteRecursively(new File("activemq-data"));
	}
}
