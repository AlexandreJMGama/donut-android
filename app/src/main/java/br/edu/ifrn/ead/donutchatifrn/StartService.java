package br.edu.ifrn.ead.donutchatifrn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ale on 03/09/2017.
 */

public class StartService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent it = new Intent(context, RestService.class);
        context.startService(it);
    }
}
