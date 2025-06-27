//
//  RulesModel.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/19/25.
//

import Foundation

struct CardRules: Codable {
    let object: String
    let hasMore: Bool
    let data: [Ruling]

    enum CodingKeys: String, CodingKey {
        case object
        case hasMore
        case data
    }

    struct Ruling: Codable {
        let object: String
        let oracleId: String
        let source: String
        let publishedAt: String
        let comment: String

        enum CodingKeys: String, CodingKey {
            case object
            case oracleId
            case source
            case publishedAt
            case comment
        }
    }
}

enum RulesError: Error {
    case invalidURL
    case invalidResponse
    case invalidData
}
