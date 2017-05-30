package org.fresheed.actionlogger.transfer;

import org.fresheed.actionlogger.data_channels.DataChannel;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by fresheed on 30.05.17.
 */

public class TransferPeer implements MessageReceiver {
    private final MessageDispatcher dispatcher;
    private final DataChannel channel;
    private final MessageProcessedCallback callback;

    public TransferPeer(MessageDispatcher dispatcher, DataChannel channel, MessageProcessedCallback callback){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.channel=channel;
        this.callback=callback;
    }

    @Override
    public void receive(Message msg) {
        if ("ACTION_LOG".equals(msg.name)){
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String time_suffix=date_format.format(new Date(System.currentTimeMillis()));
            final String name="Log_"+time_suffix;
            final ByteArrayInputStream data_stream=new ByteArrayInputStream(msg.payload);
            channel.send(name, data_stream);
            callback.inform("action log received "+new Random().nextInt());
        } else if ("ERROR".equals(msg.name)){
            callback.inform("error received "+new Random().nextInt());
        }
    }


}
