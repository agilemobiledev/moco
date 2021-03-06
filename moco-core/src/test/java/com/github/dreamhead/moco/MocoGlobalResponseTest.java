package com.github.dreamhead.moco;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.HttpProtocolVersion.VERSION_1_0;
import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Moco.proxy;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoGlobalResponseTest {
    private HttpServer server;

    @Test
    public void should_return_all_response_for_version_with_header() throws Exception {
        server = httpserver(port(), response(header("Content-Type", "text/plain")));
        server.response(version(VERSION_1_0));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("Content-Type");
                assertThat(header.getValue(), is("text/plain"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_content_with_header() throws Exception {
        server = httpserver(port(), response(header("foo", "bar")));
        server.response("hello");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outstream);
                assertThat(new String(outstream.toByteArray()), is("hello"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_header_with_header() throws Exception {
        server = httpserver(port(), response(header("foo", "bar")));
        server.response(header("blah", "param"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_status_with_header() throws Exception {
        server = httpserver(port(), response(header("foo", "bar")));
        server.response(status(200));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_and_response_handler_with_header() throws Exception {
        server = httpserver(port(), response(header("foo", "bar")));
        server.response(status(200), with(version(VERSION_1_0)));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_proxy_with_header() throws Exception {
        server = httpserver(port(), response(header("foo", "bar")));
        server.response(proxy("https://github.com/"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_mount_with_header() throws Exception {
        String MOUNT_DIR = "src/test/resources/test";
        server = httpserver(port(), response(header("foo", "bar")));
        server.mount(MOUNT_DIR, to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = Request.Get(remoteUrl("/dir/dir.response")).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_latency_with_header() throws Exception {
        server = httpserver(port(), response(header("foo", "bar")));
        server.response(latency(1, TimeUnit.SECONDS));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_seq_with_header() throws Exception {
        server = httpserver(port(), response(header("foo", "bar")));
        server.response(seq("hello", "world"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(root()).execute().returnResponse();
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));

                response = Request.Get(root()).execute().returnResponse();
                header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("bar"));
            }
        });
    }

}
