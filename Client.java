import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Check{
    public static final int STX=2;
    public static final int DATATYPE[]={90,91,92};
    public static final int ETX=3;
}
public class Client extends Thread {




    Socket socket = null;

    Client(Socket _socket) {
        this.socket = _socket;
    }
    String CID;




    String errmsg="";
    String separator="|";
    private String info="패킷 형식에 맞춰 입력해 주세요... \n" +
            "패킷 형식: 'STX|종별|패킷길이|ip1|ip2|ip3|ip4|측정년|측정월|측정일|측정시|측정분|측정초|데이터개수(n)|측정데이터*n|ETX'\n" +
            "    타입:  Int|Str| INT   |Int|Int|Int|DD|  Int | Int | Int |  I  | I   |  I |     I      |     I    | I \n" +
            "    예시:  '02|0A|16|0|0|0|1|2022|12|22|07|26|22|1|23|03'\n" +
            "           STX&ETX=02,03 고정, 종별: 0A:조도센서 0B:출입 현황 0C:수위센서 ip:ip1.ip2.ip3.ip4 ";

    Response response = new Response();//this.response메세지의 구성내용들을 넣어둔 클래스


    public void run() {

        System.out.println("연결되었다.");
        try {

            SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");//현재 시간을 출력하기 위한 함수
            OutputStream out = socket.getOutputStream();
            DataOutputStream dout = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            DataInputStream din = new DataInputStream(in);
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            StringTokenizer st;
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //response 메세지 초기화
            String ipAddress = socket.getInetAddress().toString().replace("/", "");

            Connection conn= DriverManager.getConnection(System.getenv("dburl"),System.getenv("dbid"),System.getenv("dbpassword"));
            Statement statement = conn.createStatement();
            while (true) {

                String rMsg = input.readLine();//request메세지
                ArrayList<Integer> list = new ArrayList<Integer>();
                for(int i=0;i<rMsg.length();i++){

                    list.add((int)rMsg.charAt(i));

                }


                //리퀘스트 메시지 확인용


                //request메세지 받아오기
                //만약에 받은 메시지가 공백이거나 Loop(ClientApplication.java)에서 보내는 쓰레기값일 경우 패스
                if (rMsg != null) {
                    System.out.println(list);

                    Request req=new Request(list);
                    String res=req.check(ipAddress);


                    System.out.println(res);
                    if(res.indexOf("ERR:")!=0) {
                        ResultSet test = statement.executeQuery(res);

                        while (test.next()) {

                            System.out.println(test.getString(1));
                        }
                    }


                    }




                    //this.response메세지 송신
//						dout.writeUTF(this.response.type+"///"+this.response.statecode+"///"+this.response.value+"///"+"END");


                break;
            }
        } catch (SQLException e){
            System.out.println("db접속 실패"+e);

        }

        catch (Exception e) {
            System.out.println("예외가 발생했습니다....." + e);

            System.out.println(e);
        }

    }
}


class Request {//request 메세지 세부사항

    private int stx;
    private int messageType;
    private int ipLength[]= new int[4];
    private String ipAddress="";
    private int[] time;
    private int dataLength;
    private int data=0;
    private int etx;
    Request(){

    }
    Request(ArrayList<Integer> list){
        int now=0;
        this.stx= list.get(now++);
        System.out.println(this.stx);
        this.messageType=list.get(now++);
        System.out.println(this.messageType);
        this.ipLength[0]=list.get(now++)-48;
        this.ipLength[1]=list.get(now++)-48;
        this.ipLength[2]=list.get(now++)-48;
        this.ipLength[3]=list.get(now++)-48;

        System.out.println(this.ipLength[0]+"..."+this.ipLength[1]);

        for(int i=0;i<4;i++){
            for(int j=0;j<ipLength[i];j++){
                this.ipAddress+=list.get(now++)-48;
            }
            if(i<3)
            this.ipAddress+=".";
        }
        System.out.println(this.ipAddress);
        this.time= new int[]{
                (list.get(now++)-48) * 1000+(list.get(now++)-48)*100+(list.get(now++)-48)*10+ (list.get(now++)-48)
                ,(list.get(now++)-48)*10+(list.get(now++)-48)
                ,(list.get(now++)-48)*10+ (list.get(now++)-48)
                ,(list.get(now++)-48)*10+(list.get(now++)-48)
                ,(list.get(now++)-48)*10+ (list.get(now++)-48)
                ,(list.get(now++)-48)*10+(list.get(now++)-48)};
        for(int i=0;i<this.time.length;i++){
            System.out.print(this.time[i]);
        }
        System.out.println();
        this.dataLength=list.get(now++)-48;
        System.out.println(this.dataLength);
        for(int i=0;i<this.dataLength;i++){
            data+=(list.get(now++)-48)*(int)Math.pow(10,this.dataLength-i-1);


        }
        System.out.println(this.data);
        this.etx=list.get(now++);
    }
    String check(String ipAddress){
       if(this.stx!=Check.STX){
           return "ERR:STX is Incorrect";
       }
       else if(this.etx!=Check.ETX){
           return "ERR:ETX is Incorrect";
       }
       else if(!this.ipAddress.equals(ipAddress)){
           System.out.println(this.ipAddress+"?"+ipAddress);
           return  "ERR:ipAddress is Incorrect";
       }




       switch (this.messageType){
           case 90:

       }

       return "SHOW COLUMNS FROM sundam.test_h";
    }
}

class Response {//this.response 메세지 세부사항
    int stx=3;
    int messageType=94;
    int length;
    String ipAddress[];
    String time[];
    int dataCount;
    int data[];
    String etx="03";
}

