/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package de.tu_darmstadt.ulb.record.impl;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import de.tu_darmstadt.ulb.record.GenericRecord;
import de.tu_darmstadt.ulb.record.RecordService;
import org.apache.logging.log4j.Logger;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;


/**
 * @author Qin Zhao (ULB Darmstadt)
 * @version 07.11.2024
 */
public class CsvRecordImpl implements RecordService {

    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(CsvRecordImpl.class);
    private List<GenericRecord> records;
    private ConfigurationService configurationService;
    private String defaultSeparator;
    private String filePath;
    private String separator;
    private Map<String, String> columnMappings;

    public CsvRecordImpl() {
        configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
    }

    public void setFilePath(String filePath) {
        this.filePath = configurationService.getProperty(filePath);
    }

    public void setSeparator(String separator) {
        this.separator = configurationService.getProperty(separator, defaultSeparator);
    }

    public void setDefaultSeparator(String defaultSeparator) {
        this.defaultSeparator = defaultSeparator;
    }

    public void setColumnMappings(Map<String, String> columnMappings) {
        this.columnMappings = columnMappings;
    }

    private void initRecords() {
        try {
            HeaderColumnNameTranslateMappingStrategy<GenericRecord> strategy =
                new HeaderColumnNameTranslateMappingStrategy<>();
            strategy.setType(GenericRecord.class);
            strategy.setColumnMapping(columnMappings);

            CsvToBeanBuilder<GenericRecord> builder =
                new CsvToBeanBuilder<GenericRecord>(new FileReader(filePath, StandardCharsets.UTF_8))
                    .withType(GenericRecord.class)
                    .withSeparator(separator.charAt(0))
                    .withMappingStrategy(strategy);
            records = builder.build().parse();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public GenericRecord getRecord(String key) {
        if (key != null) {
            for (GenericRecord record : getRecords()) {
                if (key.equalsIgnoreCase(record.getKey())) {
                    return record;
                }
            }
        }
        return null;
    }

    public String getValue(String key) {
        GenericRecord record = getRecord(key);
        if (record != null) {
            return record.getValue();
        }
        return null;
    }

    public List<GenericRecord> getRecords() {
        if (records == null || records.size() == 0) {
            initRecords();
        }
        return records;
    }

    public void setRecords(List<GenericRecord> records) {
        this.records = records;
    }

}
