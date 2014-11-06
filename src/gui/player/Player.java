package gui.player;

import capture.multi.raw.RawFrame;
import datastructure.ExchangeQueue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import streamapi.DataMonitor;
import streamapi.DataSink;
import streamapi.DataSource;
import streamapi.LockingMonitor;


/**
 * Created by denislavrov on 11/3/14.
 */
public class Player extends Application implements DataSink<RawFrame> {
    protected volatile boolean acceptingData = true;
    protected volatile boolean wantsData = true;
    private static final int FRAMERATE = 25;
    private PlayerController controller = null;
    private static DataSource<RawFrame> ds;
    private ExchangeQueue<RawFrame> buffer = new ExchangeQueue<>();
    public class PlayWorker extends Thread{

        @Override
        public void run() {
            super.run();
            while (acceptingData){
                while (wantsData){
                    RawFrame frame = buffer.poll();
                    if(frame != null) {
                        Platform.runLater(() -> controller.setFrame(SwingFXUtils.toFXImage(frame.bufferedImage(), null)));
                    }
                    sleepUninterrupted(1000/FRAMERATE);
                }
                sleepUninterrupted(10);
            }

        }
    }

    public Player() {
    }

    public Player(DataSource<RawFrame> ds) {
        Player.ds = ds;
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("player.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.player = this;
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        new DataMonitor<>(ds,this);
        new PlayWorker().start();
    }

    @Override
    public void consume(RawFrame data) {
        System.out.println("consuming");
        buffer.add(data);
    }

    @Override
    public boolean acceptingData() {
        return acceptingData;
    }

    @Override
    public void shutdown() {
        acceptingData = false;
    }

    @Override
    public void shutdownNow() {
        acceptingData = false;
    }

    @Override
    public boolean wantsData() {
        return wantsData && buffer.size() < 30;
    }

    public void pause(){
        wantsData = false;
    }

    public void play(){
        wantsData = true;
    }

    private static void sleepUninterrupted(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
