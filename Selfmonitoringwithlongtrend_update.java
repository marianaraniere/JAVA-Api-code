/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selfmonitoringwithlongtrend_update;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import uk.co.agena.minerva.model.Model;
import uk.co.agena.minerva.model.extendedbn.*;
import uk.co.agena.minerva.model.scenario.Scenario;
import uk.co.agena.minerva.util.model.DataPoint;
import uk.co.agena.minerva.util.model.DataSet;
import uk.co.agena.minerva.model.MarginalDataItem;
import uk.co.agena.minerva.model.MarginalDataItemList;
import uk.co.agena.minerva.util.nptgenerator.*;
import java.util.ArrayList;
import java.util.Arrays;
import uk.co.agena.minerva.model.MessagePassingLinkException;
import uk.co.agena.minerva.model.PropagationException;
import uk.co.agena.minerva.model.PropagationTerminatedException;
import uk.co.agena.minerva.model.corebn.CoreBNException;
import uk.co.agena.minerva.model.corebn.CoreBNInconsistentEvidenceException;
import uk.co.agena.minerva.util.EM.Data;
import uk.co.agena.minerva.util.EM.EMCal;
import uk.co.agena.minerva.util.io.CSVWriter;
import uk.co.agena.minerva.util.io.FileHandlingException;
import uk.co.agena.minerva.util.model.NameDescription;
import uk.co.agena.minerva.util.model.SampleDataGenerator;

/**
 *
 * @author Mariana
 */
public class Selfmonitoringwithlongtrend_update {

  public static void main(String args[]) {
        
      String csvfile = "C:\\Users\\maria\\PhD\\selfmon\\patients\\patients\\ficpt.csv";
        String line = "";
        List<String[]> result = new ArrayList<>();
        String[] data;

        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csvfile));
            line = csvReader.readLine(); // read first header line, to skip it..
            while ((line = csvReader.readLine()) != null)          
            {
                data = line.split(",");
                result.add(data);
            }
            csvReader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        // create matrix from arrray-list...
        String [][] matrix = new String[result.size()][];
        for (int i = 0; i < result.size(); i++) {
            String[] row =(result.get(i));
            matrix[i] = row;
        }
        
        //just for test..
        //System.out.println(matrix[1][1].toString());
        //System.out.println("Number of rows: " + matrix.length);
        //System.out.println("Number of columns: " + matrix[0].length);
      
           
        try {
            
            
            // First get the single BN
            Model m = Model.load("selfmonitoringBN2.cmp");
            ExtendedBN ebn = m.getExtendedBNAtIndex(0);
               
                   
            //Calculate the model
            m.calculate();
                   
            //System.out.println("Start data split");
        
            //number of patients
            int n = matrix.length;
            
            //number of time slices
            int t = 100;
            
            //Entering evidence
            
        String[][] subArrayS1 = get2DSubArray(matrix, 0,n-1,0,t-1);
        String[][] subArrayS2 = get2DSubArray(matrix, 0,n-1,t,2*t-1);
        String[][] subArrayS3 = get2DSubArray(matrix, 0,n-1,2*t,3*t-1);
        String[][] subArrayS4 = get2DSubArray(matrix, 0,n-1,3*t,4*t-1);
        String[][] subArrayS5 = get2DSubArray(matrix, 0,n-1,4*t,5*t-1);
        String[][] subArrayS6 = get2DSubArray(matrix, 0,n-1,5*t,6*t-1);
        String[][] subArrayS7 = get2DSubArray(matrix, 0,n-1,6*t,7*t-1);
        
            
        double[][] evidenceBGL = getStringtodoubleMatrix(subArrayS1);
        int[][] evidenceMed = getStringtoIntMatrix(subArrayS2);
        int[][] evidenceExe = getStringtoIntMatrix(subArrayS3);
        int[][] evidenceCar = getStringtoIntMatrix(subArrayS4);
        int[][] evidenceBlu = getStringtoIntMatrix(subArrayS5);
        int[][] evidenceHea = getStringtoIntMatrix(subArrayS6);
        int[][] evidenceDiz = getStringtoIntMatrix(subArrayS7);
                
        System.out.println(evidenceBGL[0].length);
        System.out.println(evidenceMed[0].length);
        
            //System.out.println("End data split");
                  
            //First scenario
            Scenario s = m.getScenarioAtIndex(0);
        
            //Outcomes
            double[][][] bglcontrol = new double[evidenceBGL.length][evidenceBGL[0].length-6][3];
            double[][][] breakfoverallstate = new double[evidenceBGL.length][evidenceBGL[0].length-6][3];
            double[][][] trend = new double[evidenceBGL.length][evidenceBGL[0].length-6][3];
                      
            //Naming nodes in the BN           
            LabelledEN Prior = (LabelledEN) ebn.getExtendedNodeWithName("Prior");
            LabelledEN TrendPrior = (LabelledEN) ebn.getExtendedNodeWithName("Trend prior");
            LabelledEN BGLcontrol = (LabelledEN) ebn.getExtendedNodeWithName("Breakfast BGL control");
            LabelledEN BGLoverall = (LabelledEN) ebn.getExtendedNodeWithName("Breakfast overall state");
            LabelledEN longtrendnode = (LabelledEN) ebn.getExtendedNodeWithName("Long term BGL control trend");
            LabelledEN BGLcontrol6 = (LabelledEN) ebn.getExtendedNodeWithName("Breakfast BGL control_6");
            LabelledEN longtrendnode6 = (LabelledEN) ebn.getExtendedNodeWithName("Long term BGL control trend_6");
            
            ContinuousIntervalEN BGL6 =(ContinuousIntervalEN)ebn.getExtendedNodeWithName("Capillary blood glucose measurement_6");
            ContinuousIntervalEN BGL5 =(ContinuousIntervalEN)ebn.getExtendedNodeWithName("Capillary blood glucose measurement_5");
            ContinuousIntervalEN BGL4 =(ContinuousIntervalEN)ebn.getExtendedNodeWithName("Capillary blood glucose measurement_4");
            ContinuousIntervalEN BGL3 =(ContinuousIntervalEN)ebn.getExtendedNodeWithName("Capillary blood glucose measurement_3");
            ContinuousIntervalEN BGL2 =(ContinuousIntervalEN)ebn.getExtendedNodeWithName("Capillary blood glucose measurement_2");
            ContinuousIntervalEN BGL1 =(ContinuousIntervalEN)ebn.getExtendedNodeWithName("Capillary blood glucose measurement_1");
            ContinuousIntervalEN BGLn =(ContinuousIntervalEN)ebn.getExtendedNodeWithName("Capillary blood glucose measurement");
            
            BooleanEN Med6 = (BooleanEN) ebn.getExtendedNodeWithName("Took last prescribed medication?_6");
            BooleanEN Med5 = (BooleanEN) ebn.getExtendedNodeWithName("Took last prescribed medication?_5");
            BooleanEN Med4 = (BooleanEN) ebn.getExtendedNodeWithName("Took last prescribed medication?_4");
            BooleanEN Med3 = (BooleanEN) ebn.getExtendedNodeWithName("Took last prescribed medication?_3");
            BooleanEN Med2 = (BooleanEN) ebn.getExtendedNodeWithName("Took last prescribed medication?_2");
            BooleanEN Med1 = (BooleanEN) ebn.getExtendedNodeWithName("Took last prescribed medication?_1");
            BooleanEN Medn = (BooleanEN) ebn.getExtendedNodeWithName("Took last prescribed medication?");
            
            BooleanEN Exe6 = (BooleanEN) ebn.getExtendedNodeWithName("Did exercise after meal?_6");
            BooleanEN Exe5 = (BooleanEN) ebn.getExtendedNodeWithName("Did exercise after meal?_5");
            BooleanEN Exe4 = (BooleanEN) ebn.getExtendedNodeWithName("Did exercise after meal?_4");
            BooleanEN Exe3 = (BooleanEN) ebn.getExtendedNodeWithName("Did exercise after meal?_3");
            BooleanEN Exe2 = (BooleanEN) ebn.getExtendedNodeWithName("Did exercise after meal?_2");
            BooleanEN Exe1 = (BooleanEN) ebn.getExtendedNodeWithName("Did exercise after meal?_1");
            BooleanEN Exen = (BooleanEN) ebn.getExtendedNodeWithName("Did exercise after meal?");
            
            BooleanEN Car6 = (BooleanEN) ebn.getExtendedNodeWithName("High carbohydrate meal?_6");
            BooleanEN Car5 = (BooleanEN) ebn.getExtendedNodeWithName("High carbohydrate meal?_5");
            BooleanEN Car4 = (BooleanEN) ebn.getExtendedNodeWithName("High carbohydrate meal?_4");
            BooleanEN Car3 = (BooleanEN) ebn.getExtendedNodeWithName("High carbohydrate meal?_3");
            BooleanEN Car2 = (BooleanEN) ebn.getExtendedNodeWithName("High carbohydrate meal?_2");
            BooleanEN Car1 = (BooleanEN) ebn.getExtendedNodeWithName("High carbohydrate meal?_1");
            BooleanEN Carn = (BooleanEN) ebn.getExtendedNodeWithName("High carbohydrate meal?");
            
            BooleanEN Blu6 = (BooleanEN) ebn.getExtendedNodeWithName("Blurred vision_6");
            BooleanEN Blu5 = (BooleanEN) ebn.getExtendedNodeWithName("Blurred vision_5");
            BooleanEN Blu4 = (BooleanEN) ebn.getExtendedNodeWithName("Blurred vision_4");
            BooleanEN Blu3 = (BooleanEN) ebn.getExtendedNodeWithName("Blurred vision_3");
            BooleanEN Blu2 = (BooleanEN) ebn.getExtendedNodeWithName("Blurred vision_2");
            BooleanEN Blu1 = (BooleanEN) ebn.getExtendedNodeWithName("Blurred vision_1");
            BooleanEN Blun = (BooleanEN) ebn.getExtendedNodeWithName("Blurred vision");
            
            BooleanEN Hea6 = (BooleanEN) ebn.getExtendedNodeWithName("Headache_6");
            BooleanEN Hea5 = (BooleanEN) ebn.getExtendedNodeWithName("Headache_5");
            BooleanEN Hea4 = (BooleanEN) ebn.getExtendedNodeWithName("Headache_4");
            BooleanEN Hea3 = (BooleanEN) ebn.getExtendedNodeWithName("Headache_3");
            BooleanEN Hea2 = (BooleanEN) ebn.getExtendedNodeWithName("Headache_2");
            BooleanEN Hea1 = (BooleanEN) ebn.getExtendedNodeWithName("Headache_1");
            BooleanEN Hean = (BooleanEN) ebn.getExtendedNodeWithName("Headache");
            
            BooleanEN Diz6 = (BooleanEN) ebn.getExtendedNodeWithName(" Dizzness_6");
            BooleanEN Diz5 = (BooleanEN) ebn.getExtendedNodeWithName(" Dizzness_5");
            BooleanEN Diz4 = (BooleanEN) ebn.getExtendedNodeWithName(" Dizzness_4");
            BooleanEN Diz3 = (BooleanEN) ebn.getExtendedNodeWithName(" Dizzness_3");
            BooleanEN Diz2 = (BooleanEN) ebn.getExtendedNodeWithName(" Dizzness_2");
            BooleanEN Diz1 = (BooleanEN) ebn.getExtendedNodeWithName(" Dizzness_1");
            BooleanEN Dizn = (BooleanEN) ebn.getExtendedNodeWithName(" Dizzness");
            
            //Prior
            double priorgood = 0.33;
            double priormedium = 0.33;
            double priorbad = 0.33;
            
            //Prior Trend
            double trendpriordet = 0.33;
            double trendpriorsta = 0.33;
            double trendpriorimp = 0.33;
            
            
            for (int j = 0; j < evidenceBGL.length; j++) {
            
            System.out.println("Patient " + j);
            
            //Setting the standard prior before each iteration         
            Prior.setNPT(new double[] {priorgood , priormedium ,priorbad});
            TrendPrior.setNPT(new double[] { trendpriordet,trendpriorsta,trendpriorimp });
            
            for (int i = 0; i < evidenceBGL[0].length-6; i++) {

                //Evidence for BGL values
            //Evidence for BGL values
            s.addRealObservation(ebn.getId(),BGL6.getId(),evidenceBGL[j][i] );
            s.addRealObservation(ebn.getId(),BGL5.getId(),evidenceBGL[j][i+1] );
            s.addRealObservation(ebn.getId(),BGL4.getId(),evidenceBGL[j][i+2] );
            s.addRealObservation(ebn.getId(),BGL3.getId(),evidenceBGL[j][i+3] );
            s.addRealObservation(ebn.getId(),BGL2.getId(),evidenceBGL[j][i+4] );
            s.addRealObservation(ebn.getId(),BGL1.getId(),evidenceBGL[j][i+5] );
            s.addRealObservation(ebn.getId(),BGLn.getId(),evidenceBGL[j][i+6] );
            
            //Evidence Last medication 
            s.addHardEvidenceObservation(ebn.getId(), Med6.getId(),
            Med6.getExtendedStateAtIndex(evidenceMed[j][i]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Med5.getId(),
            Med5.getExtendedStateAtIndex(evidenceMed[j][i+1]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Med4.getId(),
            Med4.getExtendedStateAtIndex(evidenceMed[j][i+2]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Med3.getId(),
            Med3.getExtendedStateAtIndex(evidenceMed[j][i+3]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Med2.getId(),
            Med2.getExtendedStateAtIndex(evidenceMed[j][i+4]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Med1.getId(),
            Med1.getExtendedStateAtIndex(evidenceMed[j][i+5]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Medn.getId(),
            Medn.getExtendedStateAtIndex(evidenceMed[j][i+6]).getId());
            
            // Evidence for Exercise
            s.addHardEvidenceObservation(ebn.getId(), Exe6.getId(),
            Exe6.getExtendedStateAtIndex(evidenceExe[j][i]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Exe5.getId(),
            Exe5.getExtendedStateAtIndex(evidenceExe[j][i+1]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Exe4.getId(),
            Exe4.getExtendedStateAtIndex(evidenceExe[j][i+2]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Exe3.getId(),
            Exe3.getExtendedStateAtIndex(evidenceExe[j][i+3]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Exe2.getId(),
            Exe2.getExtendedStateAtIndex(evidenceExe[j][i+4]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Exe1.getId(),
            Exe1.getExtendedStateAtIndex(evidenceExe[j][i+5]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Exen.getId(),
            Exen.getExtendedStateAtIndex(evidenceExe[j][i+6]).getId());
            
            //Evidence for high carbs meal
            s.addHardEvidenceObservation(ebn.getId(), Car6.getId(),
            Car6.getExtendedStateAtIndex(evidenceCar[j][i]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Car5.getId(),
            Car5.getExtendedStateAtIndex(evidenceCar[j][i+1]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Car4.getId(),
            Car4.getExtendedStateAtIndex(evidenceCar[j][i+2]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Car3.getId(),
            Car3.getExtendedStateAtIndex(evidenceCar[j][i+3]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Car2.getId(),
            Car2.getExtendedStateAtIndex(evidenceCar[j][i+4]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Car1.getId(),
            Car1.getExtendedStateAtIndex(evidenceCar[j][i+5]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Carn.getId(),
            Carn.getExtendedStateAtIndex(evidenceCar[j][i+6]).getId());

            //Evidence for Blurried vision
            s.addHardEvidenceObservation(ebn.getId(), Blu6.getId(),
            Blu6.getExtendedStateAtIndex(evidenceBlu[j][i]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Blu5.getId(),
            Blu5.getExtendedStateAtIndex(evidenceBlu[j][i+1]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Blu4.getId(),
            Blu4.getExtendedStateAtIndex(evidenceBlu[j][i+2]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Blu3.getId(),
            Blu3.getExtendedStateAtIndex(evidenceBlu[j][i+3]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Blu2.getId(),
            Blu2.getExtendedStateAtIndex(evidenceBlu[j][i+4]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Blu1.getId(),
            Blu1.getExtendedStateAtIndex(evidenceBlu[j][i+5]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Blun.getId(),
            Blun.getExtendedStateAtIndex(evidenceBlu[j][i+6]).getId());
            
           //Evidence for headache
            s.addHardEvidenceObservation(ebn.getId(), Hea6.getId(),
            Hea6.getExtendedStateAtIndex(evidenceHea[j][i]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Hea5.getId(),
            Hea5.getExtendedStateAtIndex(evidenceHea[j][i+1]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Hea4.getId(),
            Hea4.getExtendedStateAtIndex(evidenceHea[j][i+2]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Hea3.getId(),
            Hea3.getExtendedStateAtIndex(evidenceHea[j][i+3]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Hea2.getId(),
            Hea2.getExtendedStateAtIndex(evidenceHea[j][i+4]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Hea1.getId(),
            Hea1.getExtendedStateAtIndex(evidenceHea[j][i+5]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Hean.getId(),
            Hean.getExtendedStateAtIndex(evidenceHea[j][i+6]).getId());
            
            //Evidence for dizzness
            s.addHardEvidenceObservation(ebn.getId(), Diz6.getId(),
            Diz6.getExtendedStateAtIndex(evidenceDiz[j][i]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Diz5.getId(),
            Diz5.getExtendedStateAtIndex(evidenceDiz[j][i+1]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Diz4.getId(),
            Diz4.getExtendedStateAtIndex(evidenceDiz[j][i+2]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Diz3.getId(),
            Diz3.getExtendedStateAtIndex(evidenceDiz[j][i+3]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Diz2.getId(),
            Diz2.getExtendedStateAtIndex(evidenceDiz[j][i+4]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Diz1.getId(),
            Diz1.getExtendedStateAtIndex(evidenceDiz[j][i+5]).getId());
            s.addHardEvidenceObservation(ebn.getId(), Dizn.getId(),
            Dizn.getExtendedStateAtIndex(evidenceDiz[j][i+6]).getId());
            
            m.calculate();
            
            //Saving marginals of BGL control
            MarginalDataItemList mdil2 = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, BGLcontrol);
            MarginalDataItem mdi2 = mdil2.getMarginalDataItemAtIndex(0);
            List marginals2 = mdi2.getDataset().getDataPoints();
            DataPoint b21 = (DataPoint) marginals2.get(0);
            bglcontrol[j][i][0]=b21.getValue();
            DataPoint b22 = (DataPoint) marginals2.get(1);
            bglcontrol[j][i][1]=b22.getValue();
            DataPoint b23 = (DataPoint) marginals2.get(2);
            bglcontrol[j][i][2]=b23.getValue();
                                    
            //Saving marginals of BGL overall state
            MarginalDataItemList mdil3 = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, BGLoverall);
            MarginalDataItem mdi3 = mdil3.getMarginalDataItemAtIndex(0);
            List marginals3 = mdi3.getDataset().getDataPoints();
            DataPoint b31 = (DataPoint) marginals3.get(0);
            breakfoverallstate[j][i][0]=b31.getValue();
            DataPoint b32 = (DataPoint) marginals3.get(1);
            breakfoverallstate[j][i][1]=b32.getValue();
            DataPoint b33 = (DataPoint) marginals3.get(2);
            breakfoverallstate[j][i][2]=b33.getValue();
            
            //Saving marginals of Trend
            MarginalDataItemList mdil4 = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, longtrendnode);
            MarginalDataItem mdi4 = mdil4.getMarginalDataItemAtIndex(0);
            List marginals4 = mdi4.getDataset().getDataPoints();
            DataPoint b41 = (DataPoint) marginals4.get(0);
            trend[j][i][0]=b41.getValue();
            DataPoint b42 = (DataPoint) marginals4.get(1);
            trend[j][i][1]=b42.getValue();
            DataPoint b43 = (DataPoint) marginals4.get(2);
            trend[j][i][2]=b43.getValue();
                                   
            //Getting marginals for BGL control 6
            MarginalDataItemList mdil = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, BGLcontrol6);
            MarginalDataItem mdi = mdil.getMarginalDataItemAtIndex(0);
            //Prior.setNPT = mdi;
            List marginals = mdi.getDataset().getDataPoints();
            DataPoint a = (DataPoint) marginals.get(0);
            double b;
            b = a.getValue();
            DataPoint c = (DataPoint) marginals.get(1);
            double d;
            d = c.getValue();
            DataPoint e = (DataPoint) marginals.get(2);
            double f;
            f = e.getValue();
            
            //Updating the PRIOR
            Prior.setNPT(new double[] { b,d,f });
            
            //Getting marginals for long trend node 6
            MarginalDataItemList mdiltrend = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, longtrendnode6);
            MarginalDataItem mditrend = mdiltrend.getMarginalDataItemAtIndex(0);
            //Prior.setNPT = mdi;
            List marginalstrend = mditrend.getDataset().getDataPoints();
            DataPoint atrend = (DataPoint) marginalstrend.get(0);
            double btrend;
            btrend = atrend.getValue();
            DataPoint ctrend = (DataPoint) marginalstrend.get(1);
            double dtrend;
            dtrend = ctrend.getValue();
            DataPoint etrend = (DataPoint) marginalstrend.get(2);
            double ftrend;
            ftrend = etrend.getValue();
            
            //Updating the PRIOR
            TrendPrior.setNPT(new double[] { btrend,dtrend,ftrend });
                       
            System.out.println("Time " + i);
            }
            
            }
                     
         //System.out.println(Arrays.deepToString(trend));
                               
    //exporting to excel
            
    //trend
    MarginalDataItemList mdil = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, longtrendnode);
    MarginalDataItem mdi = mdil.getMarginalDataItemAtIndex(0);
    List marginals = mdi.getDataset().getDataPoints();
    
    FileWriter filetrend = new FileWriter("C:\\Users\\maria\\PhD\\selfmon\\Selfmonitoringwithlongtrend_update\\trend.csv");          
         
        for(int i = 0; i < trend.length; i++){
            for(int k = 0; k < trend[0][1].length; k++){
                filetrend.append("Patient "+i);
                filetrend.append(',');         
                DataPoint marginal = (DataPoint) marginals.get(k);
                filetrend.append(marginal.getLabel());
                filetrend.append(','); 
                for (int j=0; j< trend[0].length; j++){
                   filetrend.append(Double.toString(trend[i][j][k]));
                   filetrend.append(',');
                }
            filetrend.append('\n');
            // writer.flush();
            }
        //filetrend.append('\n');
        //writer.flush();
        }
    filetrend.close();
    
    //BGL control
    MarginalDataItemList mdil1 = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, BGLcontrol);
    MarginalDataItem mdi1 = mdil1.getMarginalDataItemAtIndex(0);
    List marginals1 = mdi1.getDataset().getDataPoints();
    
    FileWriter fileBGLcontrol = new FileWriter("C:\\Users\\maria\\PhD\\selfmon\\Selfmonitoringwithlongtrend_update\\BGLcontrol.csv");          
         
        for(int i = 0; i < bglcontrol.length; i++){
            for(int k = 0; k < bglcontrol[0][1].length; k++){
                fileBGLcontrol.append("Patient "+i);
                fileBGLcontrol.append(',');         
                DataPoint marginal1 = (DataPoint) marginals1.get(k);
                fileBGLcontrol.append(marginal1.getLabel());
                fileBGLcontrol.append(','); 
                for (int j=0; j< bglcontrol[0].length; j++){
                   fileBGLcontrol.append(Double.toString(bglcontrol[i][j][k]));
                   fileBGLcontrol.append(',');
                }
            fileBGLcontrol.append('\n');
            // writer.flush();
            }
        //fileBGLcontrol.append('\n');
        //writer.flush();
        }
    fileBGLcontrol.close();
    
    //BGL overall state
    MarginalDataItemList mdil2 = m.getMarginalDataStore().getMarginalDataItemListForNode(ebn, BGLoverall);
    MarginalDataItem mdi2 = mdil2.getMarginalDataItemAtIndex(0);
    List marginals2 = mdi2.getDataset().getDataPoints();
    
    FileWriter fileBGLoverall = new FileWriter("C:\\Users\\maria\\PhD\\selfmon\\Selfmonitoringwithlongtrend_update\\BGLoverall.csv");          
         
        for(int i = 0; i < breakfoverallstate.length; i++){
            for(int k = 0; k < breakfoverallstate[0][1].length; k++){
                fileBGLoverall.append("Patient "+i);
                fileBGLoverall.append(',');         
                DataPoint marginal2 = (DataPoint) marginals2.get(k);
                fileBGLoverall.append(marginal2.getLabel());
                fileBGLoverall.append(','); 
                for (int j=0; j<breakfoverallstate[0].length; j++){
                   fileBGLoverall.append(Double.toString(breakfoverallstate[i][j][k]));
                   fileBGLoverall.append(',');
                }
            fileBGLoverall.append('\n');
            // writer.flush();
            }
        //fileBGLoverall.append('\n');
        //writer.flush();
        }
    fileBGLoverall.close();
    
        
        } catch (Exception e) {
        };
        
  }
 
public static String[][] get2DSubArray(String[][] largeArray, int rowStartIndex, int rowEndIndex, int columnStartIndex, int columnEndIndex) {
        String[][] subArray = new String[rowEndIndex - rowStartIndex + 1][columnEndIndex - columnStartIndex + 1];
        for (int row = rowStartIndex; row < rowEndIndex+1; row++) {
                subArray[row] = Arrays.copyOfRange(largeArray[row], columnStartIndex, columnEndIndex+1);
        }
    System.out.println("Data split succeded");
    return subArray;
    }
    public static int[][] getStringtoIntMatrix(String[][] subArray){
        int[][] newSubArray = new int[subArray.length][subArray[0].length];
        for(int row = 0; row < subArray.length; row++){
            for(int col = 0; col <subArray[0].length; col++){
                newSubArray[row][col] = Integer.parseInt(subArray[row][col]);
            }
        }
        System.out.println("String to Int succeded");
        return newSubArray;
    }
    public static double[][] getStringtodoubleMatrix(String[][] subArray){
        double[][] newSubArray = new double[subArray.length][subArray[0].length];
        for(int row = 0; row < subArray.length; row++){
            for(int col = 0; col < subArray[0].length; col++){
                newSubArray[row][col] = Double.parseDouble(subArray[row][col]);
            }
        }
        System.out.println("String to double succeded");
        return newSubArray;
    }    
}


