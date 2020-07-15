package kr.co.kits.intergration.mapper

import org.apache.ibatis.annotations.Insert

import kr.co.kits.intergration.model.Distribute

interface DistributeMapper {

	@Insert("""<script>
        insert into bulk_upload_req_log(
	          cabinet
	        , object_name
	        , format
	        , content_length
			, req_type
			, network_location
			, username
			, password
			, creation_time
            , res_href
            , session_id
            , tree
        ) values (
	          #{cabinetName}
	        , #{objectName}
	        , #{format}
	        , #{contentLength}
	        , #{type}
	        , #{networkLocation}
	        , #{username}
	        , #{password}
	        , #{creationTime}
            , #{href}
            , #{sessionId}
            , #{tree}
        )
	</script>""")
	void insertDistribute(Distribute distribute);

	//	@Select("""<script>
	//        select
	//          city
	//        , name
	//        , address
	//        , zip
	//        from hotel where city = #{city}
	//	</script>""")
	//	Hotel selectByCityId(Hotel hotel);
}