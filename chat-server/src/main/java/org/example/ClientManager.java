package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientManager implements Runnable {

    private final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String name;
    public final static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            broadcastMessage("Server: " + name + " подключился к чату." );
        } catch (IOException e){

        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        //Удаление клиента из коллекции
        removeClient();
        try {
            //Завершаем работу буфера на чтение данных
            if (bufferedReader != null) bufferedReader.close();
            //Завершаем работу буфера для записи данных
            if (bufferedWriter != null) bufferedWriter.close();
            //Закрытие соеденения с клиентским сокетом
            if (socket != null) socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void removeClient(){
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        broadcastMessage("Server: " + name + " покинул чат" );
    }


    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient == null){
                    //для macOS
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                if(messageFromClient.split(" ")[1].trim().charAt(0) == '@'){
                    String findName = findNameToSendUser(messageFromClient);
                    if(listHaveFindUser(findName, clients)){
                        ClientManager clientToSend = getClientByName(findName);
                        clientToSend.bufferedWriter.write("Личное сообщение ообщение от " + messageFromClient);
                        clientToSend.bufferedWriter.newLine();
                        clientToSend.bufferedWriter.flush();
                    }
                }
                else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String message) {
            for (ClientManager client : clients) {
                try {
                    //Отпрпвить сообщение всем пользователям, кроме себя самого
                    if (!client.name.equals(name)) {
                        client.bufferedWriter.write(message + " не там");
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
    }


    private static String findNameToSendUser(String string){
        return string.split(" ")[1].substring(1, string.split(" ")[1].length());
    }

    private boolean listHaveFindUser(String findName, ArrayList<ClientManager> list){
        boolean have = false;
        for(ClientManager client : list) {
            if(client.name.equals(findName)){
                have = true;
                return have;
            }
        }
        return have;
    }

    private ClientManager getClientByName(String userName){
        for(ClientManager clientManager : clients){
            if(clientManager.name.equals(userName)) return clientManager;
        }
        return null;
    }

//    if (message.trim().charAt(0) == '@') {
//        String findName = findNameToSendUser(message);
//        if(!listHaveFindUser(findName, clients)) {
//            System.out.println("В чате нет пользователя с именем " + findName);
//        } else {
//            try {
//                //Отправить сообщение получателю с именем findName
//                ClientManager clientToSendMessage = getClientByName(findName);
//                clientToSendMessage.bufferedWriter.write(message);
//                clientToSendMessage.bufferedWriter.newLine();
//                clientToSendMessage.bufferedWriter.flush();
//            } catch (IOException e) {
//                closeEverything(socket,bufferedReader,bufferedWriter);
//            }
//        }
//    }

}
