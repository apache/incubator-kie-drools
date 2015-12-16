/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.pmml.pmml_4_2;


import org.dmg.pmml.pmml_4_2.descr.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigInteger;

public class PMMLGeneratorUtils {







    private static Marshaller initContext( PMML pmml ) throws JAXBException {
        JAXBContext pContext = JAXBContext.newInstance(pmml.getClass().getPackage().getName());
        Marshaller marshaller = pContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        return marshaller;
    }

    public static boolean streamPMML( PMML pmml, OutputStream out ) {
        try {
            Marshaller marshaller = initContext( pmml );
            marshaller.marshal( pmml, out );
            return true;
        } catch ( JAXBException e ) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean streamPMML( PMML pmml, Writer out ) {
        try {
            Marshaller marshaller = initContext( pmml );
            marshaller.marshal( pmml, out );
            return true;
        } catch ( JAXBException e ) {
            e.printStackTrace();
        }
        return false;
    }


    public static PMML generateSimpleNeuralNetwork( String modelName,
                                                    String[] inputfieldNames, String[] outputfieldNames,
                                                    double[] inputMeans, double[] inputStds,
                                                    double[] outputMeans, double[] outputStds,
                                                    int hiddenSize,
                                                    double[] weights ) {

        int counter = 0;
        int wtsIndex = 0;
        PMML pmml = new PMML();
        pmml.setVersion("4.0");

        Header header = new Header();
        Application app = new Application();
        app.setName( "Drools PMML Generator" );
        app.setVersion( "0.01 Alpha" );
        header.setApplication( app );

        header.setCopyright("BSD");

        header.setDescription(" Smart Vent Model ");

        Timestamp ts = new Timestamp();
        ts.getContent().add( new java.util.Date().toString() );
        header.setTimestamp( ts );

        pmml.setHeader( header );


        DataDictionary dic = new DataDictionary();
        dic.setNumberOfFields( BigInteger.valueOf( inputfieldNames.length + outputfieldNames.length ) );
        for ( String ifld : inputfieldNames ) {
            DataField dataField = new DataField();
            dataField.setName( ifld );
            dataField.setDataType( DATATYPE.DOUBLE );
            dataField.setDisplayName( ifld );
            dataField.setOptype( OPTYPE.CONTINUOUS );
            dic.getDataFields().add( dataField );
        }
        for ( String ofld : outputfieldNames ) {
            DataField dataField = new DataField();
            dataField.setName( ofld );
            dataField.setDataType( DATATYPE.DOUBLE );
            dataField.setDisplayName( ofld );
            dataField.setOptype( OPTYPE.CONTINUOUS );
            dic.getDataFields().add( dataField );
        }

        pmml.setDataDictionary(dic);


        NeuralNetwork nnet = new NeuralNetwork();
        nnet.setActivationFunction( ACTIVATIONFUNCTION.LOGISTIC );
        nnet.setFunctionName( MININGFUNCTION.REGRESSION );
        nnet.setNormalizationMethod( NNNORMALIZATIONMETHOD.NONE );
        nnet.setModelName( modelName );

        MiningSchema miningSchema = new MiningSchema();
        for ( String ifld : inputfieldNames ) {
            MiningField mfld = new MiningField();
            mfld.setName( ifld );
            mfld.setOptype( OPTYPE.CONTINUOUS );
            mfld.setUsageType( FIELDUSAGETYPE.ACTIVE );
            miningSchema.getMiningFields().add( mfld );
        }
        for ( String ofld : outputfieldNames ) {
            MiningField mfld = new MiningField();
            mfld.setName( ofld );
            mfld.setOptype( OPTYPE.CONTINUOUS );
            mfld.setUsageType( FIELDUSAGETYPE.PREDICTED );
            miningSchema.getMiningFields().add( mfld );
        }

        nnet.getExtensionsAndNeuralLayersAndNeuralInputs().add( miningSchema );


        Output outputs = new Output();
        for ( String ofld : outputfieldNames ) {
            OutputField outFld = new OutputField();
            outFld.setName( "Out_" + ofld );
            outFld.setTargetField( ofld );
            outputs.getOutputFields().add( outFld );
        }

        nnet.getExtensionsAndNeuralLayersAndNeuralInputs().add( outputs );


        NeuralInputs nins = new NeuralInputs();
        nins.setNumberOfInputs( BigInteger.valueOf( inputfieldNames.length ) );

        for ( int j = 0; j < inputfieldNames.length; j++ ) {
            String ifld = inputfieldNames[j];
            NeuralInput nin = new NeuralInput();
            nin.setId( "" + counter++ );
            DerivedField der = new DerivedField();
            der.setDataType( DATATYPE.DOUBLE );
            der.setOptype( OPTYPE.CONTINUOUS );
            NormContinuous nc = new NormContinuous();
            nc.setField( ifld );
            nc.setOutliers( OUTLIERTREATMENTMETHOD.AS_IS );
            LinearNorm lin1 = new LinearNorm();
            lin1.setOrig( 0 );
            lin1.setNorm( - inputMeans[j] / inputStds[j] );
            nc.getLinearNorms().add( lin1 );
            LinearNorm lin2 = new LinearNorm();
            lin2.setOrig( inputMeans[j] );
            lin2.setNorm( 0 );
            nc.getLinearNorms().add( lin2 );
            der.setNormContinuous( nc );
            nin.setDerivedField( der );
            nins.getNeuralInputs().add(nin);
        }

        nnet.getExtensionsAndNeuralLayersAndNeuralInputs().add( nins );


        NeuralLayer hidden = new NeuralLayer();
        hidden.setNumberOfNeurons( BigInteger.valueOf( hiddenSize ));

        for ( int j = 0; j < hiddenSize; j ++ ) {
            Neuron n = new Neuron();
            n.setId( "" + counter++ );
            n.setBias( weights[ wtsIndex++ ] );
            for ( int k = 0; k < inputfieldNames.length; k++ ) {
                Synapse con = new Synapse();
                con.setFrom( "" + k );
                con.setWeight( weights[ wtsIndex++ ] );
                n.getCons().add( con );
            }
            hidden.getNeurons().add( n );
        }

        nnet.getExtensionsAndNeuralLayersAndNeuralInputs().add( hidden );



        NeuralLayer outer = new NeuralLayer();
        outer.setActivationFunction( ACTIVATIONFUNCTION.IDENTITY );
        outer.setNumberOfNeurons( BigInteger.valueOf( outputfieldNames.length ));

        for ( int j = 0; j < outputfieldNames.length; j ++ ) {
            Neuron n = new Neuron();
            n.setId( "" + counter++ );
            n.setBias( weights[ wtsIndex++ ] );
            for ( int k = 0; k < hiddenSize; k++ ) {
                Synapse con = new Synapse();
                con.setFrom( "" + ( k + inputfieldNames.length ) );
                con.setWeight( weights[ wtsIndex++ ] );
                n.getCons().add( con );
            }
            outer.getNeurons().add( n );
        }

        nnet.getExtensionsAndNeuralLayersAndNeuralInputs().add( outer );



        NeuralOutputs finalOuts = new NeuralOutputs();
        finalOuts.setNumberOfOutputs( BigInteger.valueOf( outputfieldNames.length ) );
        for ( int j = 0; j < outputfieldNames.length; j ++ ) {
            NeuralOutput output = new NeuralOutput();
            output.setOutputNeuron( ""+ ( j + inputfieldNames.length + hiddenSize ) );
            DerivedField der = new DerivedField();
            der.setDataType( DATATYPE.DOUBLE );
            der.setOptype( OPTYPE.CONTINUOUS );
            NormContinuous nc = new NormContinuous();
            nc.setField( outputfieldNames[j] );
            nc.setOutliers( OUTLIERTREATMENTMETHOD.AS_IS );
            LinearNorm lin1 = new LinearNorm();
            lin1.setOrig( 0 );
            lin1.setNorm( - outputMeans[j] / outputStds[j] );
            nc.getLinearNorms().add( lin1 );
            LinearNorm lin2 = new LinearNorm();
            lin2.setOrig( outputMeans[j] );
            lin2.setNorm( 0 );
            nc.getLinearNorms().add( lin2 );
            der.setNormContinuous( nc );
            output.setDerivedField( der );
            finalOuts.getNeuralOutputs().add( output );
        }

        nnet.getExtensionsAndNeuralLayersAndNeuralInputs().add( finalOuts );


        pmml.getAssociationModelsAndBaselineModelsAndClusteringModels().add( nnet );

        return pmml;

    }
}
