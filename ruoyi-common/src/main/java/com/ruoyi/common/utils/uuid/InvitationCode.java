package com.ruoyi.common.utils.uuid;

import java.util.Random;

/**
 * @author syw
 */
public class InvitationCode {

//    public static String getCode() {
//        String code = generateCode();
//        User user = userDao.selectByCode(code);
//        if(user != null){
//            return getCode();
//        }else{
//            return code ;
//        }
//    }


    public static String generateCode() {
        String charList = "ABCDEFGHIJKLMNPQRSTUVWXY";
        String numList = "0123456789";
        String rev = "";
        int maxNumCount = 4;
        int length = 6;
        Random f = new Random();
        for (int i = 0; i < length; i++) {
            if (f.nextBoolean() && maxNumCount > 0) {
                maxNumCount--;
                rev += numList.charAt(f.nextInt(numList.length()));
            } else {
                rev += charList.charAt(f.nextInt(charList.length()));
            }
        }
        return rev;
    }

    public static void main(String[] args) {
        System.out.println("generateCode() = " + generateCode());
    }

}
