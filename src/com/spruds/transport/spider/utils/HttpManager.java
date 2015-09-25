package com.spruds.transport.spider.utils;

import com.spruds.transport.spider.service.SpiderService;
import com.spruds.transport.spider.service.data.ScheduleDataService;
import java.io.OutputStream;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpVersion;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ClientConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpManager {
    private static final int REQUEST_DELAY = 2500;
    private static final DefaultHttpClient sClient;
    private ScheduleDataService scheduleDataService;
    private Logger log = Logger.getLogger(SpiderService.class);

    static {
        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");

        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        HttpClientParams.setRedirecting(params, false);

        HttpProtocolParams.setUserAgent(params, "Public Transport, Android");

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        sClient = new DefaultHttpClient(manager, params);
    }

    private HttpManager() {
    }

    public static HttpResponse execute(HttpHead head) throws IOException {
        return sClient.execute(head);
    }

    public static HttpResponse execute(HttpHost host, HttpGet get) throws IOException {
        return sClient.execute(host, get);
    }

    public HttpResponse execute(HttpGet get) throws IOException {
        String url = get.getURI().toString();
        final InputStream is = scheduleDataService.getFetchResult(url);
        
        if(is == null) {
            log.debug("fetching " + url);
            ThreadUtils.sleep(REQUEST_DELAY);
            
            HttpResponse response = sClient.execute(get);
            String data = EntityUtils.toString(response.getEntity());
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                scheduleDataService.saveFetchResult(url, data);
            }
            return  getOKResponse(IOUtils.convertStringToStream(data));
        }
        else {
            log.debug("returning cached " + url);
            return getOKResponse(is);
        }
    }

    public static HttpResponse execute(HttpHost host, HttpPost post) throws IOException {
        return sClient.execute(host, post);
    }

    public static HttpResponse execute(HttpPost post) throws IOException {
        return sClient.execute(post);
    }

    public void setDataService(ScheduleDataService dataService) {
        this.scheduleDataService = dataService;
    }

    private HttpResponse getOKResponse(final InputStream is) {
        return new HttpResponse() {
            public HttpEntity getEntity() {
                return new HttpEntity() {
                    public InputStream getContent()
                            throws IOException, IllegalStateException {
                        return is;
                    }

                    public boolean isRepeatable() {return false;}
                    public boolean isChunked() {return false;}
                    public long getContentLength() {return 0;}
                    public Header getContentType() {return null;}
                    public Header getContentEncoding() {return null;}
                    public void writeTo(OutputStream out) throws IOException {}
                    public boolean isStreaming() {return false;}
                    public void consumeContent() throws IOException {}
                };
            }

            public StatusLine getStatusLine() {
                return new StatusLine() {
                    public int getStatusCode() {
                        return HttpStatus.SC_OK;
                    }
                    public ProtocolVersion getProtocolVersion() {return null;}
                    public String getReasonPhrase() {return null;}
                };
            }
            public void setStatusLine(StatusLine sl) {}
            public void setStatusLine(ProtocolVersion pv, int i) {}
            public void setStatusLine(ProtocolVersion pv, int i, String string) {}
            public void setStatusCode(int i) throws IllegalStateException { }
            public void setReasonPhrase(String string) throws IllegalStateException {}
            public void setEntity(HttpEntity he) {}
            public Locale getLocale() {return null;}
            public void setLocale(Locale locale) {}
            public ProtocolVersion getProtocolVersion() {return null;}
            public boolean containsHeader(String string) {return false;}
            public Header[] getHeaders(String string) {return null;}
            public Header getFirstHeader(String string) {return null;}
            public Header getLastHeader(String string) {return null;}
            public Header[] getAllHeaders() {return null;}
            public void addHeader(Header header) {}
            public void addHeader(String string, String string1) {}
            public void setHeader(Header header) {}
            public void setHeader(String string, String string1) {}
            public void setHeaders(Header[] headers) {}
            public void removeHeader(Header header) {}
            public void removeHeaders(String string) {}
            public HeaderIterator headerIterator() {return null;}
            public HeaderIterator headerIterator(String string) {return null;}
            public HttpParams getParams() {return null;}
            public void setParams(HttpParams hp) {}
        };
    }
}
