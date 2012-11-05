/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droolsjbpm.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.droolsjbpm.services.impl.model.NodeInstanceDesc;


/**
 *
 * @author salaboy
 */
public class SearchServiceImpl {

    private IndexWriter iw = null;
    private IndexSearcher is = null;

    public SearchServiceImpl() {
        try {
            SimpleAnalyzer analyzer = new SimpleAnalyzer(Version.LUCENE_40);
            iw = new IndexWriter(FSDirectory.open(new File("index/")), new IndexWriterConfig(Version.LUCENE_40, analyzer));
        } catch (IOException ex) {
            Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void index(NodeInstanceDesc node) throws IOException {
        Document d = new Document();
        
        d.add(new StringField("processName", node.getName(), Field.Store.YES));
        d.add(new StringField("nodeId", String.valueOf(node.getNodeId()), Field.Store.YES));
        d.add(new StringField("processInstanceId", String.valueOf(node.getProcessInstanceId()), Field.Store.YES));
        d.add(new StringField("sessionId", String.valueOf(node.getSessionId()), Field.Store.YES));
        //d.add(new TextField()); if I need to store large texts that requires tokenization
        // To support any value search
        d.add(new StringField("all", String.valueOf(node.getSessionId()), Field.Store.NO));
        d.add(new StringField("all", String.valueOf(node.getProcessInstanceId()), Field.Store.NO));
        d.add(new StringField("all", String.valueOf(node.getNodeId()), Field.Store.NO));
        d.add(new StringField("all", String.valueOf(node.getName()), Field.Store.NO));
        // for suggestion
        d.add(new StringField("suggest", node.getName(), Field.Store.NO));
        
        iw.updateDocument(new Term("processName"), d);
        
        DirectoryReader ireader = DirectoryReader.open(iw, true);
        is = new IndexSearcher(ireader);
        
        // iw.commit(); this should be executed ever X minutes

    }
    
    public void commit() throws IOException{
        iw.commit();
    }
    
    // q= nodeId:1 OR sessionId:10
    // q= 1234324
    public List<Map<String, String>> search(String query, int nroOfResults) throws ParseException, IOException {
        SimpleAnalyzer analyzer = new SimpleAnalyzer(Version.LUCENE_40);
        
        QueryParser parser = new QueryParser(Version.LUCENE_40, "all", analyzer);
        TopDocs search = is.search(parser.parse(query), nroOfResults);
        List<Map<String, String>> docs = new ArrayList<Map<String, String>>(search.scoreDocs.length);
        for(ScoreDoc d : search.scoreDocs){
            Document doc = is.doc(d.doc);
            Map<String, String> myDoc = new HashMap<String, String>();
            for(IndexableField f : doc){
                myDoc.put(f.name(), f.stringValue());
            }
            docs.add(myDoc);
        }
        return docs;
        
    }
    
//    
//    public List<String> suggest(String query) throws IOException{
//        AtomicReader ar = SlowCompositeReaderWrapper.wrap(is.getIndexReader());
//        Fields fields = ar.fields();
//        TermsEnum iterator = fields.terms("suggest").iterator(null);
//        
//        
//        
//    }
}
