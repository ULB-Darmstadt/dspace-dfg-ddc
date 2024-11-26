/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package de.tu_darmstadt.ulb.record;

/**
 * @author Qin Zhao (ULB Darmstadt)
 * @version 07.11.2024
 */
public interface RecordService {
    public GenericRecord getRecord(String key);
    public String getValue(String key);
}
