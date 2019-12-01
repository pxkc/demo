package com.nowcoder.demo.service;

import com.nowcoder.demo.dao.NewsDao;
import com.nowcoder.demo.model.News;
import com.nowcoder.demo.model.User;
import com.nowcoder.demo.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * @author pxk
 * @date 2019/10/18 - 15:11
 */
@Service
public class NewsService {
    @Autowired
    private NewsDao newsDao;

    public void updateCommentCount(int id, int count){
        newsDao.updateCommentCount(id, count);
    }

    public void updateLikeCount(int id, int count){
        newsDao.updateLikeCount(id, count);
    }


    public List<News> getLatesetNews(int userId, int offset, int limit) {
        return newsDao.selectByUserIdAndOffset(userId, offset, limit);
    }


    public News getById(int newsId){
        return newsDao.getById(newsId);
    }

    public String saveImage(MultipartFile file) throws IOException {
        // 判断是否是一个文件
        int dosPos = file.getOriginalFilename().lastIndexOf(".");
        if (dosPos < 0) {
            return null;
        }//找不到
        String fileExt = file.getOriginalFilename().substring(dosPos + 1);
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }

    public int addNews(News news) {
        newsDao.addNews(news);
        return news.getId();
    }
}