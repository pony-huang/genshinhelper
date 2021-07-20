package org.ponking.gih.sign;

import org.apache.http.Header;

/**
 * @Author ponking
 * @Date 2021/7/17 21:46
 */
public class WeiBoSign implements Sign {
    @Override
    public void doSign() throws Exception {

    }

    @Override
    public Header[] getHeaders() {
        return new Header[0];
    }
}
