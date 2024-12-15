/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.util;

import codex.j3map.processors.J3mapPropertyProcessor;
import codex.j3map.Numbers;

/**
 *
 * @author gary
 */
public class LongProcessor implements J3mapPropertyProcessor<Long> {

    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    public Long process(String str) {
        if (!str.endsWith("l")) {
            return null;
        }
        long out = 0;
        int sign = 1;
        str = str.substring(0, str.length() - 1);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c == '-') {
                sign = -1;
                continue;
            }
            if (c == 'l') {
                break;
            }
            Integer number = Numbers.NUMERALS.get(c);
            if (number == null) {
                return null;
            }
            out += number * (int) Math.pow(10, length - i - 1);
        }
        return out * sign;
    }

    @Override
    public String[] export(Long property) {
        return new String[]{property.toString() + "l"};
    }

}
