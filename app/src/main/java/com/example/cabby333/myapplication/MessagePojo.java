package com.example.cabby333.myapplication;

public class MessagePojo {

    private String name;
    private PortfolioActivity.ROOM_TYPES roomType;

    MessagePojo(String name, PortfolioActivity.ROOM_TYPES roomType) {
        this.name = name;
        this.roomType = roomType;
    }

    public String getName()
    {
        return name;
    }

    public String getFileName() {
        String fileName;
        if (roomType == PortfolioActivity.ROOM_TYPES.HOUSE)
            fileName = name;
        else
            fileName = name + "_" + roomType.toString();
        return fileName;
    }

    public PortfolioActivity.ROOM_TYPES getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return name + ", " + roomType.toString();
    }
}
