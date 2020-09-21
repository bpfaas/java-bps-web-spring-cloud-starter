/**
* Copyright (c) 2020 Copyright bp All Rights Reserved.
* Author: brainpoint
* Date: 2020-07-09 15:26
* Desc: 
*/

package com.bpfaas.starter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * use for cache response body.
 */
public class WebHttpBodyServletResponseWrapper extends HttpServletResponseWrapper {

  private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
  private HttpServletResponse response;

  public WebHttpBodyServletResponseWrapper(HttpServletResponse response) {
    super(response);
    this.response = response;
  }

  public byte[] getBody() {
    return byteArrayOutputStream.toByteArray();
  }

  @Override
  public ServletOutputStream getOutputStream() {
    return new ServletOutputStreamWrapper(this.byteArrayOutputStream, this.response);
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    return new PrintWriter(new OutputStreamWriter(this.byteArrayOutputStream, this.response.getCharacterEncoding()));
  }

  @AllArgsConstructor
  public static class ServletOutputStreamWrapper extends ServletOutputStream {

    @Setter @Getter
    private ByteArrayOutputStream outputStream;

    @Setter @Getter
    private HttpServletResponse response;

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setWriteListener(WriteListener listener) {

    }

    @Override
    public void write(int b) throws IOException {
      this.outputStream.write(b);
    }

    @Override
    public void flush() throws IOException {
      if (!this.response.isCommitted()) {
        byte[] body = this.outputStream.toByteArray();
        ServletOutputStream outputStream1 = this.response.getOutputStream();
        outputStream1.write(body);
        outputStream1.flush();
      }
    }
  }
}