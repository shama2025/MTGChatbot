//
//  ChatModel.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//

import Foundation

class ChatModel: ObservableObject {
    @Published var chats: [Chat] = []
}

struct Chat: Decodable, Equatable {
    let id: UUID = .init()
    let user: String
    let ai: String
}
