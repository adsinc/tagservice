namespace java tagservice.service
#@namespace scala tagservice.service

include "datamodel.thrift"

service TagService {
    void addTag(1: datamodel.Record record, 2: datamodel.Tag tag)

    void deleteTag(1: datamodel.Record record, 2: datamodel.Tag tag)

    list<datamodel.Tag> getTags(1: datamodel.Record record)

    list<datamodel.Record> getRecords(1: list<datamodel.Tag> tags)
}