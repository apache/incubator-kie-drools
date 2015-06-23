/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests.manners;

import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.io.*;
import java.util.*;

public class MannersBenchmark {
    /**
     * Number of guests at the dinner (default: 16).
     */
    private int numGuests = 16;

    /**
     * Number of seats at the table (default: 16).
     */
    private int numSeats = 16;

    /**
     * Minimum number of hobbies each guest should have (defaJavaCompilerFactoryult: 2).
     */
    private int minHobbies = 2;

    /**
     * Maximun number of hobbies each guest should have (default: 3).
     */
    private int maxHobbies = 3;

    public static void main(final String[] args) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(MannersBenchmark.class.getResourceAsStream("manners.drl")), ResourceType.DRL);

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        String filename;
        if (args.length != 0) {
            String arg = args[0];
            filename = arg;
        } else {
            filename = "manners5.dat";
        }

        for (int i = 0; i < 3; ++i) {
            InputStream is = MannersBenchmark.class.getResourceAsStream(filename);
            List list = getInputObjects(is);
            KieSession session = kbase.newStatefulKnowledgeSession();

            for (Iterator it = list.iterator(); it.hasNext();) {
                Object object = it.next();
                session.insert(object);
            }

            session.insert(new Count(1));

            long start = System.currentTimeMillis();
            session.fireAllRules();
            System.err.println(System.currentTimeMillis() - start);
            session.dispose();
        }
    }

    /**
     * Convert the facts from the <code>InputStream</code> to a list of
     * objects.
     */
    protected static List getInputObjects(InputStream inputStream) throws IOException {
        List list = new ArrayList();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0 || line.trim().startsWith(";")) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(line,
                    "() ");
            String type = st.nextToken();

            if ("guest".equals(type)) {
                if (!"name".equals(st.nextToken())) {
                    throw new IOException("expected 'name' in: " + line);
                }
                String name = st.nextToken();
                if (!"sex".equals(st.nextToken())) {
                    throw new IOException("expected 'sex' in: " + line);
                }
                String sex = st.nextToken();
                if (!"hobby".equals(st.nextToken())) {
                    throw new IOException("expected 'hobby' in: " + line);
                }
                String hobby = st.nextToken();

                Guest guest = new Guest(name,
                        Sex.resolve(sex),
                        Hobby.resolve(hobby));

                list.add(guest);
            }

            if ("last_seat".equals(type)) {
                if (!"seat".equals(st.nextToken())) {
                    throw new IOException("expected 'seat' in: " + line);
                }
                list.add(new LastSeat(new Integer(st.nextToken()).intValue()));
            }

            if ("context".equals(type)) {
                if (!"state".equals(st.nextToken())) {
                    throw new IOException("expected 'state' in: " + line);
                }
                list.add(new Context(st.nextToken()));
            }
        }
        inputStream.close();

        return list;
    }

    private InputStream generateData() {
        final String LINE_SEPARATOR = System.getProperty("line.separator");

        StringWriter writer = new StringWriter();

        int maxMale = numGuests / 2;
        int maxFemale = numGuests / 2;

        int maleCount = 0;
        int femaleCount = 0;

        // init hobbies
        List hobbyList = new ArrayList();
        for (int i = 1; i <= maxHobbies; i++) {
            hobbyList.add("h" + i);
        }

        Random rnd = new Random();
        for (int i = 1; i <= numGuests; i++) {
            char sex = rnd.nextBoolean() ? 'm' : 'f';
            if (sex == 'm' && maleCount == maxMale) {
                sex = 'f';
            }
            if (sex == 'f' && femaleCount == maxFemale) {
                sex = 'm';
            }
            if (sex == 'm') {
                maleCount++;
            }
            if (sex == 'f') {
                femaleCount++;
            }

            List guestHobbies = new ArrayList(hobbyList);

            int numHobbies = minHobbies + rnd.nextInt(maxHobbies - minHobbies + 1);
            for (int j = 0; j < numHobbies; j++) {
                int hobbyIndex = rnd.nextInt(guestHobbies.size());
                String hobby = (String) guestHobbies.get(hobbyIndex);
                writer.write("(guest (name n" + i + ") (sex " + sex + ") (hobby " + hobby + "))" + LINE_SEPARATOR);
                guestHobbies.remove(hobbyIndex);
            }
        }
        writer.write("(last_seat (seat " + numSeats + "))" + LINE_SEPARATOR);

        writer.write(LINE_SEPARATOR);
        writer.write("(context (state start))" + LINE_SEPARATOR);

        return new ByteArrayInputStream(writer.getBuffer().toString().getBytes());
    }

}
