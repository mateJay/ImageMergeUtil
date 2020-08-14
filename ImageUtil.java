package cn.bluepulse.util.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageUtil {

    /**
     * 合并完成的图片宽度或高度已最大的那张图片为准。
     * 默认合成后的图片为rgb色彩空间模式
     * @param imgList 图片list
     * @param isHorizontal 水平还是竖直方向
     * @return 合并完成后的buf img对象信息
     */
    public static MergeImageInfo mergeImages(List<BufferedImage> imgList, boolean isHorizontal) {
        List<ImageInfo> imageInfoList = new ArrayList<>();
        for (BufferedImage image : imgList) {
            int w = image.getWidth();
            int h = image.getHeight();
            int[] imageArray = new int[w * h];
            // 逐行扫描图像中各个像素的RGB到数组中
            image.getRGB(0, 0, w, h, imageArray, 0, w);

            System.out.println("图片类型：" + image.getType());

            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setWidth(w);
            imageInfo.setHeight(h);
            imageInfo.setRgbArr(imageArray);

            imageInfoList.add(imageInfo);
        }

        BufferedImage destImage;
        int width;
        int height;
        int cnt = 0;
        if (isHorizontal) {
            // 水平方向
            width = imageInfoList.stream().mapToInt(ImageInfo::getWidth).sum();
            height = imageInfoList.stream().map(ImageInfo::getHeight).max(Integer::compareTo).get();
            destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int startX = 0;
            for (ImageInfo imageInfo : imageInfoList) {
                destImage.setRGB(startX, 0, imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getRgbArr(), 0, imageInfo.getWidth());
                startX += imageInfo.getWidth();
                cnt++;
            }
        } else {
            // 竖直方向
            width = imageInfoList.stream().map(ImageInfo::getWidth).max(Integer::compareTo).get();
            height = imageInfoList.stream().mapToInt(ImageInfo::getHeight).sum();
            destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int startY = 0;
            for (ImageInfo imageInfo : imageInfoList) {
                destImage.setRGB(0, startY, imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getRgbArr(), 0, imageInfo.getWidth());
                startY += imageInfo.getHeight();
                cnt++;
            }
        }

        return new MergeImageInfo(width, height, cnt, destImage);
    }


    /**
     * 本地图片获取bufferedImage
     * @param fileLocalPath
     * @return
     * @throws IOException
     */
    public static BufferedImage getBufferedImageByLocal(String fileLocalPath) throws IOException {
        File f = new File(fileLocalPath);
        return ImageIO.read(f);
    }

    /**
     * 远程图片转BufferedImage
     * @param remoteUrl 远程图片地址
     * @return
     */
    public static BufferedImage getBufferedImageByRemoteUrl(String remoteUrl) throws IOException {
        URL url = new URL(remoteUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return ImageIO.read(conn.getInputStream());
    }


    /**
     * 输出图片
     * @param buffImg  图像拼接叠加之后的BufferedImage对象
     * @param savePath 图像拼接叠加之后的保存路径
     * @param format 图片格式
     */
    public static void generateSaveFile(BufferedImage buffImg, String savePath, String format) {
        try {
            File outFile = new File(savePath);
            if(!outFile.exists()){
                outFile.createNewFile();
            }
            ImageIO.write(buffImg, format, outFile);
            System.out.println("ImageIO write...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认jpeg格式。利于网络传输
     * @param buffImg
     * @param savePath
     */
    public static void generateSaveFile(BufferedImage buffImg, String savePath) {
        generateSaveFile(buffImg, savePath, "jpeg");
    }

    /**
     * 获取图片的byte数组
     * @param buffImg
     * @param format
     * @return
     * @throws IOException
     */
    public static byte[] generateByteArr(BufferedImage buffImg, String format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(buffImg, format, out);
        return out.toByteArray();
    }

    /**
     * 默认jpeg格式。利于网络传输
     * @param buffImg
     * @return
     * @throws IOException
     */
    public static byte[] generateByteArr(BufferedImage buffImg) throws IOException {
        return generateByteArr(buffImg, "jpeg");
    }

    /**
     * merge完成后的图片信息
     */
    static class MergeImageInfo {
        private Integer width;
        private Integer height;
        private Integer cnt;
        private BufferedImage mergeImage;

        public MergeImageInfo(Integer width, Integer height, Integer cnt, BufferedImage mergeImage) {
            this.width = width;
            this.height = height;
            this.cnt = cnt;
            this.mergeImage = mergeImage;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getCnt() {
            return cnt;
        }

        public void setCnt(Integer cnt) {
            this.cnt = cnt;
        }

        public BufferedImage getMergeImage() {
            return mergeImage;
        }

        public void setMergeImage(BufferedImage mergeImage) {
            this.mergeImage = mergeImage;
        }
    }

    /**
     * 存储图片信息
     */
    static class ImageInfo {
        private Integer width;
        private Integer height;
        private int[] rgbArr;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public int[] getRgbArr() {
            return rgbArr;
        }

        public void setRgbArr(int[] rgbArr) {
            this.rgbArr = rgbArr;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage1 = getBufferedImageByRemoteUrl("图片远程路径");
        BufferedImage bufferedImage2 = getBufferedImageByLocal("图片本地路径");

        List<BufferedImage> imageList = new ArrayList<>();
        imageList.add(bufferedImage1);
        imageList.add(bufferedImage1);
        imageList.add(bufferedImage1);

        MergeImageInfo mergeImageInfo = mergeImages(imageList, false);

        generateSaveFile(mergeImageInfo.getMergeImage(), "文件保存路径", "jpeg");
    }
}
