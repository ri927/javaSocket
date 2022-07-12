import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

//スレッド部（各クライアントに応じて）
class ServerThread extends Thread {
    private int number;//自分の番号
    private Socket incoming;
    private InputStreamReader myIsr;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private String myName;//接続者の名前

    private static boolean isGameStart = false;
    private static String question ; //お題
    private static String genre = ""; //お題のジャンル
    private static String questioner = ""; //出題者

    PokemonSearcher pokeSearch = new PokemonSearcher();
    ArrayList<PokemonItem> itemList = pokeSearch.getItemList();

    AnimalSearcher aniSearcher = new AnimalSearcher();
    ArrayList<String> animalList = aniSearcher.getItemList();

    CountrySearcher countrySearcher = new CountrySearcher();
    ArrayList<String> countryList = countrySearcher.getItemList();

    YasaiSearcher yasaiSearcher = new YasaiSearcher();
    ArrayList<String> yasaiList = YasaiSearcher.getYasaiList();





    public ServerThread(int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;
    }

    public void run() {
        try {

            //全員が抜けたらゲームを強制終了
            if(MyServer.getUserName().size() == 0){
                ServerThread.isGameStart = false;
            }


            myName = myIn.readLine();//初めて接続したときの一行目は名前

            MyServer.addUser((myName));

            MyServer.SendAll("server:"  + myName + "さんが入室しました", myName);

            //誰かが接続したら全員に名前のリストを送る
            MyServer.SendAll(MyServer.createUserList(MyServer.getUserName() ), myName);

            while (true) {//無限ループで，ソケットへの入力を監視する

                String str = myIn.readLine();

                int cmdIndex = str.indexOf(":");
                int endIndex = str.length();
                String cmd = str.substring(0, cmdIndex);//メッセージに付与されているコマンド
                String recvStr = str.substring(cmdIndex + 1, endIndex);//コマンドを除去した受け取ったメッセージ

                //描画している時
                if (cmd.equals("point")) {
                    System.out.println(+number + "(" + myName + "), Point: " + recvStr);
                    MyServer.SendAll("point:" + recvStr, myName);//サーバに来たメッセージは接続しているクライアント全員に配る
                }

                if (cmd.equals("msg")) {
                    MyServer.SendAll("msg:" + recvStr, myName);//サーバに来たメッセージは接続しているクライアント全員に配る

                    if (isGameStart) {
                        String[] answerList = recvStr.split(",");
                        String answerUserName = answerList[0];
                        String answer = answerList[1];

                        System.out.println("test1");
                        System.out.println(answer + "=" + question);
                        if (question.equals(answer)) {
                            System.out.println("test2");
                            MyServer.SendAll("server:" + answerUserName + "さん正解です", myName);//正解者の情報を全員に配る
                            MyServer.SendAll("server: 正解は" + answer + "です", myName);//正解者の情報を全員に配る
                            MyServer.SendAll("game:endGame", myName);//ゲーム終了の情報を全員に配る
                            MyServer.SendAll("server:ゲームが終了しました", myName);//ゲーム開始の情報を全員に配る
                            isGameStart = false;
                        }
                    }
                }
                if (cmd.equals("game")) {
                    if (recvStr.equals("startGame")) {

                        if (!ServerThread.isGameStart) {
                            ServerThread.isGameStart = true;
                            MyServer.SendAll("game:startGame", myName);//ゲーム開始の情報を全員に配る
                            //ゲームが開始されたらログをリセット
                            MyServer.SendAll("server:ゲームが開始されます", myName);//ゲーム開始の情報を全員に配る
                            MyServer.SendAll("server:待機中...", myName);//ゲーム開始の情報を全員に配る

                            //ゲームが始まったときランダムに出題者を決め、クライアントに送信
                            questioner = MyServer.getRandomQuestioner(MyServer.getUserName());
                            MyServer.SendAll("questioner:" + questioner, myName);//出題者の情報を全員に配る

                        }
                    } else if (recvStr.equals("endGame")) {
                        if (ServerThread.isGameStart) {
                            ServerThread.isGameStart = false;
                            MyServer.SendAll("game:endGame", myName);//ゲーム終了の情報を全員に配る
                            MyServer.SendAll("server:ゲームが終了しました", myName);//ゲーム開始の情報を全員に配る
                        }
                    }
                }
                if (cmd.equals("clear")) {
                    MyServer.SendAll("clear:canvasClear", myName);//キャンバスリセットの情報を全員に配る
                }
                if (cmd.equals("question")) {
                    switch (recvStr) {
                        case "pokemon":
                            question = pokeSearch.getRandomPokemon(itemList);
                            genre = "ポケモン";
                            break;

                        case "animal":
                            question = aniSearcher.getRandomAnimal(animalList);
                            genre = "動物(カタカナ)";
                            break;

                        case "country":
                            question = countrySearcher.getRandomCountry(countryList);
                            genre = "国名";
                            break;

                        case "yasai":
                            question = yasaiSearcher.getRandomYasai(yasaiList);
                            genre = "野菜名(漢字/カナ)";
                            break;
                    }

                    //出題
                    MyServer.SendAll("question:" + question, myName);//お題の情報を全員に配る
                    MyServer.SendAll("server:ゲームを開始します", myName);//出題者の情報を全員に配る
                    MyServer.SendAll("server:出題者は" + questioner + "さんです", myName);//出題者の情報を全員に配る
                    MyServer.SendAll("server:お題のジャンルは" + genre + "です", myName);//出題者の情報を全員に配る

                }

                //全員が抜けたらゲームを強制終了
                if (MyServer.getUserName().size() == 0) {
                    ServerThread.isGameStart = false;
                }

                System.out.println("Received from client No." + number + "(" + myName + "), Messages: " + str + "(" + cmd + ")");

                if (str != null) {//このソケット（バッファ）に入力があるかをチェック
                    if (str.toUpperCase().equals("BYE")) {
                        myOut.println("Good bye!");
                        break;

                    }
                }
            }
        } catch (Exception e) {
            //ここにプログラムが到達するときは，接続が切れたとき
            System.out.println("Disconnect from client No."+number+"("+myName+")");
            MyServer.removeUser(myName);
            MyServer.SetFlag(number, false);//接続が切れたのでフラグを下げる
            //誰かが切れたらユーザリストを更新するために全員に新しいリストを送信
            MyServer.SendAll(MyServer.createUserList(MyServer.getUserName() ), myName);

            MyServer.SendAll("server:"  + myName + "さんが退室しました", myName);

        }
    }
}

class MyServer{

    private static int maxConnection=10;//最大接続数
    private static Socket[] incoming;//受付用のソケット
    private static boolean[] flag;//接続中かどうかのフラグ
    private static InputStreamReader[] isr;//入力ストリーム用の配列
    private static BufferedReader[] in;//バッファリングをによりテキスト読み込み用の配列
    private static PrintWriter[] out;//出力ストリーム用の配列
    private static ServerThread[] myServerThread;//スレッド用の配列
    private static int member;//接続しているメンバーの数

    //参加中のユーザを管理するリスト
    private static ArrayList<String> userName = new ArrayList<>();

    //全員にメッセージを送る
    public static void SendAll(String str, String myName){
        //送られた来たメッセージを接続している全員に配る
        for(int i=1;i<=member;i++){
            if(flag[i] == true){
                out[i].println(str);
                out[i].flush();//バッファにある全てのデータをすぐに送信する
                System.out.println("Send messages to client No."+i);
            }
        }
    }

    //フラグの設定を行う
    public static void SetFlag(int n, boolean value){
        flag[n] = value;
    }

    //ユーザーを管理するリストにユーザーを追加
    public static void addUser(String user){
        userName.add(user);
    }
    //ユーザを削除
    public static void removeUser(String user){
        userName.remove(user);
    }

    public static ArrayList<String> getUserName(){return userName;}

    public static String getRandomQuestioner(ArrayList<String> userList){
        ArrayList<String> randomUserlist = (ArrayList<String>) userList.clone();
        Collections.shuffle(randomUserlist);
        return randomUserlist.get(0);
    }

    public static String createUserList(ArrayList<String> user){
        String userList = "user:";
        for(String name : user){
            userList += name + ",";
        }
        return userList;
    }

    public static void main(String[] args) {
        //必要な配列を確保する
        incoming = new Socket[maxConnection];
        flag = new boolean[maxConnection];
        isr = new InputStreamReader[maxConnection];
        in = new BufferedReader[maxConnection];
        out = new PrintWriter[maxConnection];

        myServerThread = new ServerThread[maxConnection];

        int n = 1;
        member = 0;//誰も接続していないのでメンバー数は０

        try {
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("The server has launched!");
            System.out.println("IP Address     : " + addr.getHostAddress());
            ServerSocket server = new ServerSocket(10000);//10000番ポートを利用する

            while (true) {
                incoming[n] = server.accept();
                flag[n] = true;
                System.out.println("Accept client No." + n);
                //必要な入出力ストリームを作成する
                isr[n] = new InputStreamReader(incoming[n].getInputStream());
                in[n] = new BufferedReader(isr[n]);
                out[n] = new PrintWriter(incoming[n].getOutputStream(), true);

                myServerThread[n] = new ServerThread(n, incoming[n], isr[n], in[n], out[n]);//必要なパラメータを渡しスレッドを作成
                myServerThread[n] .start();//スレッドを開始する
                member = n;//メンバーの数を更新する
                n++;
            }

        } catch (Exception e) {
            System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
    }
}


