package br.edu.ifrn.ead.donutchatifrn.Adapters;

import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;
import br.edu.ifrn.ead.donutchatifrn.RoomChat;

/**
 * Created by Ale on 19/09/2017.
 */

public class Chat {

    public String content;
    public int userId;

    public Chat(String content, int userId){
        this.content = content;
        this.userId = userId;
    }
}
