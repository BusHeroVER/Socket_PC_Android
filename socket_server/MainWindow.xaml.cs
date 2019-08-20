using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Windows;


namespace socket
{
    /// <summary>
    /// MainWindow.xaml 的互動邏輯
    /// </summary>
    public partial class MainWindow : Window
    {

        static Socket serverSocket = null;
        static List<Socket> sockets = new List<Socket>();

        public MainWindow()
        {  
            InitializeComponent();
            startListen();
        }

        private void startListen()
        {
            IPAddress ip = IPAddress.Parse("192.168.2.105");
            serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            serverSocket.Bind(new IPEndPoint(ip, 9487));
            serverSocket.Listen(10);
            
            Thread myThread = new Thread(ListenClientConnect);
            myThread.Start();
        }

        public void ListenClientConnect()
        {
            while (true)
            {
                Socket clientSocket = serverSocket.Accept();
                sockets.Add(clientSocket);
                
                Thread receiveThread = new Thread(ReceiveMessage);
                receiveThread.Start(clientSocket);
            }
        }

        private void ReceiveMessage(object clientSocket)
        {
            Socket connection = (Socket)clientSocket;
            text1.Dispatcher.BeginInvoke(new Action(() => { text1.Text += "connect.\n"; }), null);

            while (true)
            {
                try
                {
                    byte[] result = new byte[1024];
                    int receiveNumber = connection.Receive(result);
                    String recStr = Encoding.ASCII.GetString(result, 0, receiveNumber);
                    
                    IPAddress clientIP = (connection.RemoteEndPoint as IPEndPoint).Address;
                    int clientPort = (connection.RemoteEndPoint as IPEndPoint).Port;

                    String sendStr = "Client IP : " + clientIP + ". Clinet Port : " + clientPort + ".\n" + recStr;

                    foreach (Socket socket in sockets)
                    {
                        socket.Send(Encoding.ASCII.GetBytes(sendStr));
                    }
                    
                    text1.Dispatcher.BeginInvoke(new Action(() => { text1.Text += sendStr; }), null);
                }
                catch (Exception ex)
                {
                    connection.Shutdown(SocketShutdown.Both);
                    connection.Close();
                    break;
                }
            }
        }
    }
    
}
