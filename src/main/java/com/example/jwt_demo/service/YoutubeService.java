package com.example.jwt_demo.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private YouTube getService() throws Exception {
        return new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                request -> {})
                .setApplicationName("youtube-time-machine")
                .build();
    }

    public List<Map<String, String>> getChannelVideos(String channelId) throws Exception {
        YouTube youtube = getService();

        List<String> parts = List.of("contentDetails"); 
        YouTube.Channels.List channelRequest = youtube.channels().list(parts);
        channelRequest.setId(List.of(channelId));
        channelRequest.setKey(apiKey);

        ChannelListResponse channelResult = channelRequest.execute();
        String uploadsPlaylistId = channelResult
                .getItems()
                .get(0)
                .getContentDetails()
                .getRelatedPlaylists()
                .getUploads();

        YouTube.PlaylistItems.List playlistRequest = youtube.playlistItems()
                .list(List.of("snippet"));
        playlistRequest.setPlaylistId(uploadsPlaylistId);
        playlistRequest.setKey(apiKey);
        playlistRequest.setMaxResults(25L);

        PlaylistItemListResponse playlistResult = playlistRequest.execute();

        List<Map<String, String>> videos = new ArrayList<>();
        for (PlaylistItem item : playlistResult.getItems()) {
            Map<String, String> video = new HashMap<>();
            video.put("videoId", item.getSnippet().getResourceId().getVideoId());
            video.put("title", item.getSnippet().getTitle());
            video.put("thumbnail", item.getSnippet().getThumbnails().getDefault().getUrl());
            videos.add(video);
        }

        return videos;
    }

    
 
    public List<Map<String, String>> searchChannelsByName(String query) throws Exception {
        YouTube youtube = getService();

        YouTube.Search.List searchRequest = youtube.search().list(List.of("snippet"));
        searchRequest.setQ(query);
        searchRequest.setType(List.of("channel"));
        searchRequest.setMaxResults(50L);
        searchRequest.setOrder("relevance");
        searchRequest.setKey(apiKey);

        SearchListResponse searchResponse = searchRequest.execute();

        List<String> channelIds = searchResponse.getItems()
                .stream()
                .map(item -> item.getSnippet().getChannelId())
                .collect(Collectors.toList());

        if (channelIds.isEmpty()) return Collections.emptyList();

        YouTube.Channels.List channelRequest = youtube.channels()
                .list(List.of("snippet", "statistics"));
        channelRequest.setId(channelIds);
        channelRequest.setKey(apiKey);

        ChannelListResponse channelListResponse = channelRequest.execute();

        List<Map<String, String>> channels = new ArrayList<>();
        for (Channel channel : channelListResponse.getItems()) {
            Map<String, String> c = new HashMap<>();
            c.put("channelId", channel.getId());
            c.put("title", channel.getSnippet().getTitle());
            c.put("description", channel.getSnippet().getDescription());
            c.put("thumbnail", channel.getSnippet().getThumbnails().getDefault().getUrl());
            c.put("subscriberCount", channel.getStatistics().getSubscriberCount().toString());
            channels.add(c);
        }

        channels.sort((a, b) -> Long.compare(
                Long.parseLong(b.get("subscriberCount")),
                Long.parseLong(a.get("subscriberCount"))
        ));

        return channels;
    }
}