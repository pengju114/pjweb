/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author lzw
 */
public class ImageUtility {

    public static class RandomCodeImageRanderer {

        public static final String RANDOM_STRING = "RandomString";
        private int charCount;
        private int fontSize;
        private ArrayList<Character> chars;
        private Random random;

        /**
         *
         * @param charCount 字符个数
         * @param fontSize 字体大小
         */
        public RandomCodeImageRanderer(int charCount, int fontSize) {
            this.fontSize = fontSize;
            this.charCount = charCount;
            chars = new ArrayList<Character>(10);
            random = new Random();
        }

        public RandomCodeImageRanderer() {
            this(5, 16);
        }

        public void setCharCount(int count) {
            this.charCount = count;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        /**
         * 在没有调用此方法之前不能生成验证码
         *
         * @return 返回一个新生成的验证码字符串，每调用一次就生成新的随机字符串
         */
        public String getRandomString() {
            char c;
            StringBuilder buf = new StringBuilder();
            char[] _chars = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'P', 'A', 'S', 'D', 'F', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M'};

            chars.clear();
            for (int i = 0; i < this.charCount; i++) {
                c = _chars[random.nextInt(_chars.length)];
                chars.add(new Character(c));
                buf.append(c);
            }
            return buf.toString();
        }

        /**
         * 生成随机颜色
         *
         * @return
         */
        private Color getRandomColor(int b, int e) {
            if (b > 255) {
                b = 255;
            }
            if (e > 255) {
                e = 255;
            }
            int r = b + random.nextInt(e - b);
            int g = b + random.nextInt(e - b);
            int _b = b + random.nextInt(e - b);
            return new Color(r, g, _b);
        }

        /**
         *
         * @param out 目标输出流
         */
        public void renderImage(OutputStream out) {
            if (chars.size() <= 0) {
                return;
            }
            //width 图像宽度,height 图像高度
            int width = fontSize * charCount, height = fontSize + 2;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // 获取图形上下文
            Graphics g = image.getGraphics();



            // 设定背景色
            g.setColor(getRandomColor(200, 250));
            g.fillRect(0, 0, width, height);

            //设定字体
            g.setFont(new Font("Times New Roman", Font.PLAIN, fontSize));

            // 随机产生100条干扰线，使图象中的认证码不易被其它程序探测到
            g.setColor(getRandomColor(160, 200));

            int x1, y1, x2, y2;
            for (int i = 0; i < 100; i++) {
                x1 = random.nextInt(width);
                y1 = random.nextInt(height);
                x2 = random.nextInt(12);
                y2 = random.nextInt(12);
                g.drawLine(x1, y1, x1 + x2, y1 + y2);
            }



            // 取随机产生的认证码(4位数字)
            for (int i = 0; i < chars.size(); i++) {
                // 将认证码显示到图象中
                g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));//调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
                g.drawString(String.valueOf(chars.get(i).charValue()), 13 * i + 6, 16);
            }

            // 图象生效
            g.dispose();

            // 输出图象到页面
            try {
                ImageIO.write(image, "JPEG", out);
            } catch (IOException e) {
            }
        }
    }
}
