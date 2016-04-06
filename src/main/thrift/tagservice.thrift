namespace java tagservice.service
#@namespace scala tagservice.service

include "datamodel.thrift"

exception TagServiceException {
    1: string msg
}

service TagService {
    void addTag(1: i64 recordId, 2: i64 tagId) throws(1: TagServiceException ex)

    void deleteTag(1: i64 recordId, 2: i64 tagId) throws(1: TagServiceException ex)

    list<datamodel.Tag> getTags(1: i64 recordId) throws(1: TagServiceException ex)

    list<datamodel.Record> getRecords(1: list<i64> tagIds) throws(1: TagServiceException ex)

    i64 createRecord(1: datamodel.Record record)

    i64 createTag(1: datamodel.Tag tag) throws(1: TagServiceException ex)
}