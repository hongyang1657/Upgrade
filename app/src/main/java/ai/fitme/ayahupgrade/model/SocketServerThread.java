package ai.fitme.ayahupgrade.model;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerThread extends Thread {

  private ServerSocket serverSocket;
  private Socket client;
  private BufferedOutputStream out;
  private String currCMD;

  @Override
  public void run() {
    try {
      Log.d("fy", "等待连接");
      System.out.println("---------socket 通信线程----等待连接");
      serverSocket = new ServerSocket(13000);

      while (true) {

        client = serverSocket.accept();

        out = new BufferedOutputStream(client.getOutputStream());



        // 开启子线程去读去数据
        new Thread(new SocketReadThread(new BufferedInputStream(client.getInputStream()))).start();//另外开启一个线程去读数据

      }
    } catch (IOException e) {
      e.printStackTrace();

    }
  }

  //   暴露给外部调用写入流的方法  如:SocketServerThread.SendMsg(str)
  public void SendMsg(String msg) {

    String msg_1 = msg;      //回写给银行的数据
    try {

      out.write(msg_1.getBytes("UTF-8"));
      out.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  class SocketReadThread implements Runnable {

    private BufferedInputStream in;

    public SocketReadThread(BufferedInputStream inStream) throws IOException {

      this.in = inStream;
    }

    public void run() {
      try {
        String readMsg = "";
        while (true) {
          try {
            if (!client.isConnected()) {
              break;
            }

            //   读到后台发送的消息  然后去处理
            currCMD = readMsgFromSocket(in);
            //    处理读到的消息(主要是身份证信息),然后保存在sp中;

            if (currCMD.length() == 0) {



              break;
            }
            if (readMsg .equals("0002")) {
            }
            //  将要返回的数据发送给 pc
            out.write((readMsg + "flag").getBytes());
            out.flush();


          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        in.close();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
      }
    }

    public String readMsgFromSocket(InputStream in) {
      int MAX_BUFFER_BYTES = 2048;
      String msg = "";
      byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
      try {
        int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
        msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");

      } catch (Exception e) {
        e.printStackTrace();
      }
      return msg;
    }
  }
}
