package club.yunzhi.xplaneproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@SpringBootApplication
@RestController
public class XplaneproxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(XplaneproxyApplication.class, args);
	}

	@GetMapping("X-Plane 12.00b1/directory.txt.zip")
	public String get(@RequestParam String ttl, @RequestParam String sig,
										HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, URISyntaxException, InterruptedException {
		String uri = "http://s5r5u9a2.stackpathcdn.com/X-Plane%2012.00b1/directory.txt.zip?ttl=" +
				ttl + "&" +
				"sig=" + sig;
		//		https://openjdk.org/groups/net/httpclient/intro.html
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(uri))
			.GET().build();

		HttpResponse<String> response =
				client.send(request, HttpResponse.BodyHandlers.ofString());
		return "hello";
	}

	@GetMapping("hello")
	public String hello() {
		return "hello";
	}
}
