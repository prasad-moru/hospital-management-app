package com.hospital.management.model;
/** Normalized paging and search request. */
public final class PageRequest{private final int pageNumber,pageSize;private final String searchTerm;public PageRequest(int page,int size,String search){pageNumber=Math.max(1,page);pageSize=Math.min(100,Math.max(1,size));searchTerm=search==null?"":search.trim();}public int getPageNumber(){return pageNumber;}public int getPageSize(){return pageSize;}public String getSearchTerm(){return searchTerm;}public int getOffset(){return(pageNumber-1)*pageSize;}}
