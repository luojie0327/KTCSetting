package com.ktc.setting.helper;

import java.io.UnsupportedEncodingException;

public class StringEncoderUtil {

    public static boolean isUTF8(CharSequence charSequence) {
        try {
            byte[] utf = ((String) charSequence).getBytes("ISO-8859-1");
            int ix = 0;
            while (ix < utf.length) {
                char c = (char) utf[ix];
                if ((c & 128) == 0) {
                    ix++;
                } else if ((c & 224) == 192) {
                    if (utf.length < 2 || (utf[ix + 1] & 192) != 128) {
                        return false;
                    }
                    ix += 2;
                } else if ((c & 240) == 224) {
                    if (utf.length < 3 || (utf[ix + 1] & 192) != 128 || (utf[ix + 2] & 192) != 128) {
                        return false;
                    }
                    ix += 3;
                } else if ((c & 248) == 240) {
                    if (utf.length < 4 || (utf[ix + 1] & 192) != 128 || (utf[ix + 2] & 192) != 128 || (utf[ix + 3] & 192) != 128) {
                        return false;
                    }
                    ix += 4;
                } else if ((c & 251) != 248 || utf.length < 5 || (utf[ix + 1] & 192) != 128 || (utf[ix + 2] & 192) != 128 || (utf[ix + 3] & 192) != 128 || (utf[ix + 4] & 192) != 128) {
                    return false;
                } else {
                    ix += 5;
                }
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static String convertString(CharSequence charSequence, String targetCharSet) {
        try {
            return new String(((String) charSequence).getBytes("ISO-8859-1"), targetCharSet);
        } catch (UnsupportedEncodingException e) {
            return (String) charSequence;
        }
    }
}
