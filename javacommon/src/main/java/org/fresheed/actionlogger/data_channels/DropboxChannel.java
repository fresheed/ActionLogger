package org.fresheed.actionlogger.data_channels;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by fresheed on 31.01.17.
 */

public class DropboxChannel implements DataChannel {
    private final String access_token = "1DnFiiCwVXAAAAAAAAAAJciG0Z2GDvSLsN-uBGKXPkI1D2X48SGcdIP5QyWWY7ci";

    @Override
    public void send(String name, InputStream data) {
        DbxRequestConfig config = new DbxRequestConfig(
                "AccTest/1.0", Locale.getDefault().toString());
        DbxClientV2 client = new DbxClientV2(config, access_token);
        try {
            FileMetadata metadata = client.files().uploadBuilder("/"+name).uploadAndFinish(data);
        } catch (DbxException exc){
            System.out.println("error on dropbox api call:"+exc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
