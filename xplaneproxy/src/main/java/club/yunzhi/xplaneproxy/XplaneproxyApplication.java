package club.yunzhi.xplaneproxy;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.util.EntityUtils;
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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


@SpringBootApplication
@RestController
public class XplaneproxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(XplaneproxyApplication.class, args);
	}

	@GetMapping("X-Plane 12.00b1/directory.txt.zip")
	public void get(@RequestParam String ttl, @RequestParam String sig,
										HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
		/* Custom DNS resolver */
		DnsResolver dnsResolver = new SystemDefaultDnsResolver() {
			@Override
			public InetAddress[] resolve(final String host) throws UnknownHostException {
				if (host.equalsIgnoreCase("s5r5u9a2.stackpathcdn.com")) {
            /* If we match the host we're trying to talk to,
               return the IP address we want, not what is in DNS */
					return new InetAddress[] { InetAddress.getByName("98.159.108.57") };
				} else {
					/* Else, resolve it as we would normally */
					return super.resolve(host);
				}
			}
		};

		/* HttpClientConnectionManager allows us to use custom DnsResolver */
		BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(
    /* We're forced to create a SocketFactory Registry.  Passing null
       doesn't force a default Registry, so we re-invent the wheel. */
				RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())
						.register("https", SSLConnectionSocketFactory.getSocketFactory())
						.build(),
				null, /* Default ConnectionFactory */
				null, /* Default SchemePortResolver */
				dnsResolver  /* Our DnsResolver */
		);

		/* build HttpClient that will use our DnsResolver */
		String uri = "s5r5u9a2.stackpathcdn.com/X-Plane%2012.00b1/directory.txt.zip?ttl=" +
				ttl + "&" +
				"sig=" + sig;
		HttpGet httpGet = new HttpGet("http://s5r5u9a2.stackpathcdn.com/X-Plane%2012.00b1/directory.txt.zip?ttl=" +
				ttl + "&" +
				"sig=" + sig);
		httpGet.setHeader("user-agent", httpServletRequest.getHeader("user-agent"));
		httpGet.setHeader("user-agent", httpServletRequest.getHeader("user-agent"));
		CloseableHttpClient httpclient = HttpClientBuilder.create()
				.setConnectionManager(connManager)
				.build();
		try {
			HttpHost target = new HttpHost(uri);
			HttpHost proxy = new HttpHost("127.0.0.1", 7890, "http");

			RequestConfig config = RequestConfig.custom()
					.setProxy(proxy)
					.build();
			HttpGet request = new HttpGet("/X-Plane%2012.00b1/directory.txt.zip?ttl=" +
			ttl + "&" +
					"sig=" + sig);
			request.setHeader("user-agent", httpServletRequest.getHeader("user-agent"));
			request.setHeader("accept", httpServletRequest.getHeader("accept"));
			request.setConfig(config);

			System.out.println(httpServletRequest.getReader().readLine());

			System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

			CloseableHttpResponse response = httpclient.execute(target, request);
			try {
				response.getEntity().getContent();
				httpServletResponse.setContentLength((int)response.getEntity().getContentLength());
				httpServletResponse.setStatus(response.getStatusLine().getStatusCode());
				httpServletResponse.getOutputStream().write(response.getEntity().getContent().readAllBytes());
				httpServletResponse.getOutputStream().flush();
				httpServletResponse.getOutputStream().close();
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				System.out.println(EntityUtils.toString(response.getEntity()));
				System.out.println("----------------------------------------");
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}


	private void test(String ttl, String sig, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws IOException, URISyntaxException, InterruptedException{
		String uri = "http://s5r5u9a2.stackpathcdn.com/X-Plane%2012.00b1/directory.txt.zip?ttl=" +
				ttl + "&" +
				"sig=" + sig;

		Enumeration<String> headers = httpServletRequest.getHeaderNames();
		//		https://openjdk.org/groups/net/httpclient/intro.html
		HttpClient client = HttpClient.newBuilder()
				.proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 7890)))
				.build();
//
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(uri))
				.header("user-agent", httpServletRequest.getHeader("user-agent"))
				.header("accept", httpServletRequest.getHeader("accept"))
				.GET().build();

		HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
		Map<String, List<String>> respheaders = response.headers().map();
//
//		httpServletResponse.getOutputStream().write(response.body());
//		httpServletResponse.getOutputStream().flush();
	}
	@GetMapping("hello")
	public String hello() {
		return "hello";
	}
}
