namespace java tagservice.service
#@namespace scala tagservice.service

include "datamodel.thrift"

service TagService {
    void addTag(1: i64 recordId, 2: i64 tagId)

    void deleteTag(1: i64 recordId, 2: i64 tagId)

    list<datamodel.Tag> getTags(1: i64 recordId)

    list<datamodel.Record> getRecords(1: list<i64> tagIds)

    i64 createRecord(1: datamodel.Record record)

    i64 createTag(1: datamodel.Tag tag)
}