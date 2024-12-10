'use client';
import { useEffect, useState } from "react";
import LeftMenu from '../../../../components/leftmenu/left-menu';
import * as Fetch from '../../../../components/Functions';
import Link from "next/link";
import Post from '../../../../components/PostContainer';


export default function PostPage({ params }) {
	const { postId } = params;
	const [postData, setPostData] = useState([]);

	useEffect(() => {
	  const init = async () => {
	    const post = await Fetch.getPostById(postId);
	    setPostData(post);
	  };
	  init();
	}, [postId]);


	return (
		<>
		<div className="flex gap-6 pt-6">
			<Post postData={postData} />
		</div>
		</>
	);
};