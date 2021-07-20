package org.ponking.gih.sign;

import org.apache.http.Header;

/**
 * @Author ponking
 * @Date 2021/7/17 21:24
 */
public interface Sign {

    void doSign() throws Exception;

    Header[] getHeaders();
}
