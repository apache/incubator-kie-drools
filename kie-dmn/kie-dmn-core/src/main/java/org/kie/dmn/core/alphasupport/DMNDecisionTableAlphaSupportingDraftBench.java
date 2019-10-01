/*
* Copyright 2019 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.kie.dmn.core.alphasupport;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.KieHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 200)
@Measurement(iterations = 100)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DMNDecisionTableAlphaSupportingDraftBench {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(DMNDecisionTableAlphaSupportingDraftBench.class);
    private DMNRuntime runtime;
    private DMNModel dmnModel;
    private String existingCustomer;
    private BigDecimal score;

    @Param({"0", "1", "2", "3", "4", "5", "10", "15", "20", "30", "40", "52"})
    private int alphalength;
    private char[] alphabet;

    @Setup()
    public void init() throws Exception {
        char[] az = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        this.alphabet = Arrays.copyOf(az, alphalength);
        System.setProperty("alphalength", Integer.toString(alphalength));
        
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(DMNDecisionTableAlphaSupportingDraftBench.class, "");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        
        Template temp = cfg.getTemplate("alphasupport.dmn.ftlh");
        StringWriter out = new StringWriter();
        Map<String, Object> root = new HashMap<>();
        root.put("letters", alphabet);
        temp.process(root, out);
        String xml = out.getBuffer().toString();

        LOG.debug("{}", xml);

        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources()
                                                                      .newByteArrayResource(xml.getBytes())
                                                                      .setTargetPath("src/main/resources/alphasupport.dmn"));
        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c0cf6e20-0b43-43ce-9def-c759a5f86df2", "DMN Specification Chapter 11 Example Reduced");
    }

    @Setup(Level.Iteration)
    public void initIterationValues() {
        this.existingCustomer = existingCustomer();
        this.score = new BigDecimal(Double.valueOf((Math.random() * (140 - 70)) + 70).intValue());
    }

    public String existingCustomer() {
        int randomIdx = new Random().nextInt(alphabet.length + 2);
        if (randomIdx < alphabet.length) {
            return String.valueOf(alphabet[randomIdx]);
        } else {
            return (randomIdx - alphabet.length) == 0 ? "true" : "false";
        }
    }

    @Benchmark
    public DMNResult doTest() {
        final DMNContext context = runtime.newContext();
        context.set("Existing Customer", existingCustomer);
        context.set("Application Risk Score", score);
        DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", evaluateAll);
        return evaluateAll;
    }

    public void testSimpleDecision() {
        final DMNResult dmnResult = doTest();
        LOG.debug("{}", dmnResult);
    }

    public static void main(String[] args) throws Exception {
        DMNDecisionTableAlphaSupportingDraftBench u = new DMNDecisionTableAlphaSupportingDraftBench();
        u.alphalength = 2;
        u.init();
        for (int i = 0; i < 1000; i++) {
            u.initIterationValues();
            u.doTest();
        }
        System.out.println("done.");
    }
}
