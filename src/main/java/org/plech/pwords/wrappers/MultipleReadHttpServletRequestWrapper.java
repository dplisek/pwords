package org.plech.pwords.wrappers;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class MultipleReadHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private String charEnc;
    private byte[] body;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public MultipleReadHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(request.getInputStream(), bos);
        body = bos.toByteArray();
        charEnc = request.getCharacterEncoding() != null ? request.getCharacterEncoding() : "UTF-8";
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bis.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                try {
                    readListener.onDataAvailable();
                } catch (IOException e) {
                    throw new RuntimeException("Couldn't correctly wrap ByteArrayInputStream into ServletInputStream.", e);
                }
            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body), charEnc));
    }
}
