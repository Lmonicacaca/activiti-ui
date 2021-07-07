package org.activiti.app.service.mongo;

import com.mongodb.gridfs.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@DependsOn("gridFsTemplate")
public class MongoService {

    @Autowired
    GridFsTemplate gridFsTemplate;

    public String store(InputStream inputStream, String name) {

        GridFSFile xml = gridFsTemplate.store(inputStream, name, "xml");
        Query query = new Query();
        query.addCriteria(Criteria.where("filename").is(name).and("uploadDate").lt(xml.getUploadDate()));
        gridFsTemplate.delete(query);
        return xml.getId().toString();
    }

}
